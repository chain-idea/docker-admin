package com.gzqylc.docker.admin.web.logger;

import com.gzqylc.lang.tool.SystemTool;

public class LoggerConstants {

    public static final String WEBSOCKET_URL_PREFIX = "/ws/log/";

    public static String getLogFileRoot() {
        if (SystemTool.isWindows()) {
            return "d:\\logs\\pipeline\\";
        }
        return "/logs/pipeline/";
    }

    public static String getLogPath(String id) {
        return getLogFileRoot() + id + ".log";
    }

}
