package com.mz.jarboot.service.impl;

import com.mz.jarboot.api.event.JarbootEvent;
import com.mz.jarboot.api.event.Subscriber;
import com.mz.jarboot.api.event.WorkspaceChangeEvent;
import com.mz.jarboot.api.service.ServiceManager;
import com.mz.jarboot.base.AgentManager;
import com.mz.jarboot.api.constant.CommonConst;
import com.mz.jarboot.common.CacheDirHelper;
import com.mz.jarboot.common.JarbootThreadFactory;
import com.mz.jarboot.common.notify.AbstractEventRegistry;
import com.mz.jarboot.common.notify.FrontEndNotifyEventType;
import com.mz.jarboot.common.notify.NotifyReactor;
import com.mz.jarboot.common.utils.StringUtils;
import com.mz.jarboot.event.*;
import com.mz.jarboot.task.TaskRunCache;
import com.mz.jarboot.api.pojo.ServiceInstance;
import com.mz.jarboot.api.pojo.ServiceSetting;
import com.mz.jarboot.service.TaskWatchService;
import com.mz.jarboot.utils.MessageUtils;
import com.mz.jarboot.utils.PropertyFileUtils;
import com.mz.jarboot.utils.SettingUtils;
import com.mz.jarboot.utils.TaskUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author majianzheng
 */
@Component
public class TaskWatchServiceImpl implements TaskWatchService, Subscriber<ServiceFileChangeEvent> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final int MIN_MODIFY_WAIT_TIME = 3;
    private static final int MAX_MODIFY_WAIT_TIME = 600;

    @Autowired
    private TaskRunCache taskRunCache;
    @Autowired
    private ServiceManager serverMgrService;
    @Autowired
    private AbstractEventRegistry eventRegistry;

    private final String jarbootHome = System.getProperty(CommonConst.JARBOOT_HOME);

    @Value("${jarboot.file-shake-time:5}")
    private long modifyWaitTime;
    @Value("${jarboot.after-start-exec:}")
    private String afterStartExec;
    @Value("${jarboot.services.enable-auto-start-after-start:false}")
    private boolean enableAutoStartServices;
    private String curWorkspace;
    private boolean starting = false;
    private final ThreadFactory threadFactory = JarbootThreadFactory
            .createThreadFactory("jarboot-tws", true);
    private Thread monitorThread = threadFactory.newThread(this::initPathMonitor);

    /** 阻塞队列，监控到目录变化则放入队列 */
    private final LinkedBlockingQueue<String> modifiedServiceQueue = new LinkedBlockingQueue<>(1024);

    @Override
    public void init() {
        if (starting) {
            return;
        }
        if (modifyWaitTime < MIN_MODIFY_WAIT_TIME || modifyWaitTime > MAX_MODIFY_WAIT_TIME) {
            modifyWaitTime = 5;
        }
        starting = true;
        curWorkspace = SettingUtils.getWorkspace();
        //路径监控生产者
        this.monitorThread.start();

        //路径监控消费者
        NotifyReactor.getInstance().registerSubscriber(this);

        //attach已经处于启动的进程
        this.attachRunningServer();

        if (enableAutoStartServices) {
            logger.info("Auto starting services...");
            serverMgrService.oneClickStart();
        }

        //启动后置脚本
        if (StringUtils.isNotEmpty(afterStartExec)) {
            threadFactory
                    .newThread(() -> TaskUtils.startTask(afterStartExec, null, jarbootHome))
                    .start();
        }
        //注册工作空间改变事件订阅
        NotifyReactor.getInstance().registerSubscriber(new Subscriber<WorkspaceChangeEvent>() {
            @Override
            public void onEvent(WorkspaceChangeEvent event) {
                eventRegistry.receiveEvent(eventRegistry.createTopic(WorkspaceChangeEvent.class), event);
                changeWorkspace(event.getWorkspace());
            }

            @Override
            public Class<? extends JarbootEvent> subscribeType() {
                return WorkspaceChangeEvent.class;
            }
        });
    }

    /**
     * 工作空间改变
     *
     * @param workspace 工作空间
     */
    private void changeWorkspace(String workspace) {
        MessageUtils.globalEvent(FrontEndNotifyEventType.WORKSPACE_CHANGE);
        //中断原监控线程
        monitorThread.interrupt();
        try {
            //等待原监控线程中断结束
            monitorThread.join(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (monitorThread.isAlive()) {
            logger.error("工作空间监控线程退出失败！");
            MessageUtils.error("工作空间监控线程中断失败，请重启Jarboot重试！");
            return;
        }
        this.curWorkspace = workspace;
        this.monitorThread = threadFactory.newThread(this::initPathMonitor);
        this.monitorThread.start();
    }

    @Override
    public void onEvent(ServiceFileChangeEvent event) {
        //取出后
        HashSet<String> services = new HashSet<>();
        try {
            String serviceName;
            //防抖去重，总是延迟一段时间（抖动时间配置），变化多次计一次
            while (null != (serviceName = modifiedServiceQueue.poll(modifyWaitTime, TimeUnit.SECONDS))) {
                services.add(serviceName);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        //过滤掉jar文件未变化掉服务，判定jar文件掉修改时间是否一致
        List<String> list = services.stream().filter(this::checkJarUpdate).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(list)) {
            final String msg = "监控到工作空间文件更新，开始重启相关服务...";
            MessageUtils.info(msg);
            serverMgrService.restartService(list);
        }
    }

    @Override
    public Executor executor() {
        return TaskUtils.getTaskExecutor();
    }

    @Override
    public Class<? extends JarbootEvent> subscribeType() {
        return ServiceFileChangeEvent.class;
    }

    private void attachRunningServer() {
        List<ServiceInstance> runningServers = taskRunCache.getServiceList();
        if (CollectionUtils.isEmpty(runningServers)) {
            return;
        }
        runningServers.forEach(this::doAttachRunningServer);
    }

    /**
     * 启动目录变动监控
     */
    private void initPathMonitor() {
        //先遍历所有jar文件，将文件的最后修改时间记录下来
        storeCurFileModifyTime();
        //启动路径监控
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            //初始化路径监控
            final Path monitorPath = Paths.get(curWorkspace);
            //给path路径加上文件观察服务
            monitorPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
            //开始监控
            pathWatchMonitor(watchService);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            MessageUtils.error("工作空间监控异常：" + ex.getMessage());
        } catch (InterruptedException ex) {
            //线程退出
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 缓存文件的时间戳
     */
    private void storeCurFileModifyTime() {
        File[] serverDirs = taskRunCache.getServiceDirs();
        if (null == serverDirs || serverDirs.length <= 0) {
            return;
        }
        File recordDir = CacheDirHelper.getMonitorRecordDir();
        if (!recordDir.exists() && !recordDir.mkdirs()) {
            logger.error("创建record目录（{}）失败！", recordDir.getPath());
            return;
        }
        //清理失效的record文件
        File[] recordFiles = recordDir.listFiles();
        HashMap<String, File> recordFileMap = new HashMap<>(16);
        if (null != recordFiles && recordFiles.length > 0) {
            for (File recordFile : recordFiles) {
                recordFileMap.put(recordFile.getName(), recordFile);
            }
        }
        for (File serverDir : serverDirs) {
            Collection<File> files = FileUtils
                    .listFiles(serverDir, new String[]{CommonConst.JAR_FILE_EXT}, true);
            if (!CollectionUtils.isEmpty(files)) {
                File recordFile = getRecordFile(serverDir.getPath());
                if (null != recordFile) {
                    recordFileMap.remove(recordFile.getName());
                    Properties properties = new Properties();
                    files.forEach(jarFile -> properties.put(genFileHashKey(jarFile),
                            String.valueOf(jarFile.lastModified())));
                    PropertyFileUtils.storeProperties(recordFile, properties);
                }
            }
        }
        if (!recordFileMap.isEmpty()) {
            recordFileMap.forEach((k, v) -> FileUtils.deleteQuietly(v));
        }
    }

    private File getRecordFile(String serverPath) {
        File recordFile = CacheDirHelper.getMonitorRecordFile(SettingUtils.createSid(serverPath));
        if (!recordFile.exists()) {
            try {
                if (!recordFile.createNewFile()) {
                    logger.warn("createNewFile({}) failed.", recordFile.getPath());
                    return null;
                }
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
                return null;
            }
        }
        return recordFile;
    }

    private void pathWatchMonitor(WatchService watchService) throws InterruptedException {
        for (;;) {
            final WatchKey key = watchService.take();
            for (WatchEvent<?> watchEvent : key.pollEvents()) {
                handlePathEvent(watchEvent);
            }
            if (!key.reset()) {
                logger.error("处理失败，重置错误");
                MessageUtils.error("工作空间监控异常，请重启Jarboot解决！");
                break;
            }
        }
    }

    private void handlePathEvent(WatchEvent<?> watchEvent) throws InterruptedException {
        final WatchEvent.Kind<?> kind = watchEvent.kind();
        if (kind == StandardWatchEventKinds.OVERFLOW) {
            return;
        }
        String service = watchEvent.context().toString();
        //创建事件或修改事件
        if (kind == StandardWatchEventKinds.ENTRY_CREATE ||
                kind == StandardWatchEventKinds.ENTRY_MODIFY) {
            final String path = curWorkspace + File.separator + service;
            String sid= SettingUtils.createSid(path);
            //创建或修改文件
            if (!AgentManager.getInstance().isOnline(sid)) {
                //当前不处于正在运行的状态
                return;
            }
            ServiceSetting setting = PropertyFileUtils.getServiceSettingByPath(path);
            if (Boolean.TRUE.equals(setting.getJarUpdateWatch()) && Objects.equals(sid, setting.getSid())) {
                //启用了路径监控配置
                modifiedServiceQueue.put(service);
                NotifyReactor.getInstance().publishEvent(new ServiceFileChangeEvent());
            }
        }
        //删除事件
        if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
            logger.info("触发了删除事件，忽略执行，文件：{}", watchEvent.context());
        }
    }

    private String genFileHashKey(File jarFile) {
        String path = jarFile.getPath();
        return String.format("hash.%d", path.hashCode());
    }

    private boolean checkJarUpdate(String path) {
        File serverDir = new File(path);
        Collection<File> files = FileUtils.listFiles(serverDir, new String[]{CommonConst.JAR_FILE_EXT}, true);
        if (CollectionUtils.isEmpty(files)) {
            return false;
        }
        File recordFile = getRecordFile(path);
        if (null == recordFile) {
            return false;
        }
        Properties recordProps = PropertyFileUtils.getProperties(recordFile);
        boolean updateFlag = false;
        for (File file : files) {
            String key = genFileHashKey(file);
            String value = recordProps.getProperty(key, "-1");
            long lastModifyTime = Long.parseLong(value);
            if (lastModifyTime != file.lastModified()) {
                recordProps.setProperty(key, String.valueOf(file.lastModified()));
                updateFlag = true;
            }
        }
        if (updateFlag) {
            //更新jar文件掉最后修改时间
            PropertyFileUtils.storeProperties(recordFile, recordProps);
        }
        return updateFlag;
    }

    private void doAttachRunningServer(ServiceInstance server) {
        if (AgentManager.getInstance().isOnline(server.getSid())) {
            //已经是在线状态
            return;
        }
        TaskUtils.attach(server.getSid());
    }
}
