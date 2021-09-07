// src/locales/en-US.js
export default {
    'navbar.lang': '中文',

    //Tab标题
    SERVICES_MGR: 'Services',
    SERVICES_CONF: 'Service config',
    AUTH_CONTROL: 'Authority Control',
    SETTING: 'Setting',
    HELP_DOC: 'Help',
    MENU_DOCS: 'DOCS',

    //服务管理
    ONE_KEY_START: 'Start All',
    ONE_KEY_STOP: 'Stop All',
    ONE_KEY_RESTART: 'Restart All',
    NAME: 'Name',
    STATUS: 'Status',
    CLEAR: 'Clear',
    CLOSE: 'Close',
    CMD_PLACEHOLDER: 'Input command to execute',
    SELECT_UPLOAD_SERVER_TITLE: 'Input the server name of update or new',
    UPLOAD_STAGE_TITLE: 'Upload {server} files',
    //进程状态
    RUNNING: 'Running',
    STOPPED: 'Stopped',
    STARTING: 'Starting',
    STOPPING: 'Stopping',

    //通用
    TYPE: 'Type',
    SUBMIT_BTN: 'Submit',
    RESET_BTN: 'Reset',
    REFRESH_BTN: 'Refresh',
    SERVER_EMPTY: 'The servers is empty now, please put jar file and refresh.',
    MODIFY: 'Modify',
    DELETE: 'Delete',
    CREATE: 'Create',
    SUCCESS: 'Success!',
    LOADING: 'Loading...',

    //服务配置
    SERVER_LIST_TITLE: 'Services',
    JAR_LABEL: 'Start jar file',
    JVM_OPT_LABEL: 'JVM options',
    MAIN_ARGS_LABEL: 'Main args',
    WORK_HOME_LABEL: 'Work home',
    ENV_LABEL: 'environment',
    PRIORITY_LABEL: 'Priority',
    DAEMON_LABEL: 'Daemon',
    JAR_UPDATE_WATCH_LABEL: 'File path Watch',

    //全局配置界面
    SERVERS_PATH: 'Root path',
    DEFAULT_VM_OPT: 'Default VM options',
    MAX_START_TIME: 'Max start time',

    //用户登录
    USER_NAME: 'User',
    PASSWORD: 'Password',
    LOGIN: 'Login',
    MODIFY_PWD: 'Modify password',
    CREATE_USER: 'Create user',
    SIGN_OUT: 'Sign out',
    INTERNAL_SYS_TIP: `Internal system.`,
    INTERNAL_SYS_TIP1: `Not exposed to the public network`,
    REPEAT_PASSWORD: 'Please input repeat password!',
    INPUT_PASSWORD: 'Please input password!',
    INPUT_USERNAME: 'Please input your user name!',
    INPUT_ROLE: 'Please input role!',
    PWD_NOT_MATCH: 'The two passwords that you entered do not match!',
    USER_LIST: 'User List',
    ROLE_MGR: 'Role Management',
    PRIVILEGE_MGR: 'Privilege Management',
    ROLE: 'Role',
    BIND_ROLE: 'Binding roles',
    DELETE_USER: `Do you want to delete this user({user})?`,
    DELETE_ROLE: `Do you want to delete this role?`,
    CAN_NOT_REMOVE_SELF: 'Can not delete current user!',

    //交互提示信息
    SELECT_ONE_SERVER_INFO: 'Please select one server to operate.',
    NAME_NOT_EMPTY: `Name can't be empty`,
    UPLOAD_FILE_EMPTY: `Upload file of done is empty.`,
    SELECT_ONE_OP: 'Please select one item to operate.',

    //帮助
    BASIC: 'Basic',
    QUICK_START: 'Quick start',
    ADVANCED: 'Advanced',
    COMMAND_LIST: 'Command list',
    PROP_FILE: 'Property file',
    //快速开始
    QUICK_START_P1: `Project homepage: {github} or Gitee image warehouse`,
    QUICK_START_P2: `When you enter this interface, it means that you have built and start it, and the next thing you need to do is put the jar files you developed into the directory. By default, no configuration is made，create a folder named {dir} in the directory jarboot as the service root path. In this path create the fold which it's name as it's service name, then put the jar files under sub file.`,
    QUICK_START_P3: `Default directory structure: `,
    QUICK_START_P4: `You can modify the default path by {key2} in the {key1} interface. You need to set the jar file name which include the Main Class by {key4} in the {key3} interface when you have multiple jar files.`,
    QUICK_START_P5: `Start the service in {key} interface.`,
    QUICK_START_P6: `Click the corresponding button to complete the start and stop of all services.`,
    QUICK_START_P7: `You can also select one or several services and click the corresponding button to start and stop the selected service.`,
    QUICK_START_P8: `Refresh the list of services, update the number and status of services.`,
    //设置
    SETTING_P1: `General configuration`,
    SETTING_P2: `:Set the default service root directory. You can change the default service root directory location. By default, it is the services directory under jarboot.`,
    SETTING_P3: `:When the service does not set the startup parameters of the JVM, the default global configuration is used.`,
    SETTING_P4: `:When the service process does not output information for a long time after it is started, it is considered that the start is completed.`,
    SETTING_P5: `:Hidden function. The target service can be debugged through the third party's Arthas on the interface. The address is:`,
    SETTING_P6: `:When there are multiple jar files in the service directory, you need to specify which jar file to start, that is, the name of the jar file where main class is located.`,
    SETTING_P7: `:Specify the JVM parameters when the service starts, such as memory size, garbage collector, etc.`,
    SETTING_P8: `:The start parameter, that is, the parameter passed to the main entry function.`,
    SETTING_P9: `:The higher the start priority, the higher the start priority, and the parallel start priority with the same value will wait for the service of the previous level to start before the next level starts. When the service is shut down, it stops in reverse order.`,
    SETTING_P10: `:Specifies whether the service needs to be guarded. If it is enabled, jarboot will restart the service automatically when it is shut down abnormally.`,
    SETTING_P11: `:Specify whether to monitor the update of the service directory. After opening, if the file under the service directory is monitored to be updated, the service will be automatically restarted. If it is updated many times in a period of time, it will only be restarted at the end (anti jitter design). In addition, it is recommended that temporary files such as logs should not be in the directory of the service, because the update of sub files will be misjudged as an update with executable files.`,
    //进阶
    USAGE_DEMO: 'Use reference',
    PROP_FILE_DESC: 'Property file jarboot.properties',
    CMD_LIST_DESC: `Execute debug command`,
    CMD_LIST_HELP: `Debug command supported, more help goto online document.`,
};
