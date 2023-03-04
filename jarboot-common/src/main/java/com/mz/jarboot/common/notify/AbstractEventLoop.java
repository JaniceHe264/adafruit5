package com.mz.jarboot.common.notify;

/**
 * @author majianzheng
 */
@SuppressWarnings("all")
public abstract class AbstractEventLoop extends Thread {
    private volatile boolean initialized = false;
    protected volatile boolean shutdown = false;

    protected AbstractEventLoop() {
        this("jarboot.event.loop");
    }

    protected AbstractEventLoop(String name) {
        setDaemon(true);
        setName(name);
        start();
    }

    @Override
    public synchronized void start() {
        if (!initialized) {
            // start just called once
            super.start();
            initialized = true;
        }
    }

    @Override
    @SuppressWarnings("all")
    public final void run() {
        try {
            for (; ; ) {
                if (shutdown) {
                    break;
                }
                loop();
            }
        } catch (Throwable ex) {
            //ignore
        }
    }

    /**
     * 事件循环
     */
    protected abstract void loop();
}