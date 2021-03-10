package com.gzqylc.docker.admin.web.logger;

import com.github.dockerjava.api.model.ResponseItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * 1. 文件日志优化，流只打开一次
 * 2. 文件删除策略
 */
@Slf4j
public class FileLogger {


    Map<String, File> fileDict = new HashMap<>();


    private StringBuffer buffer = new StringBuffer();

    private FileLogger(String... ids) {
        for (String id : ids) {
            File logFile = new File(LoggerConstants.getLogPath(id));
            if (!logFile.exists()) {
                try {
                    FileUtils.forceMkdirParent(logFile);
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            fileDict.put(id, logFile);
        }

    }

    public static FileLogger getLogger(String... ids) {
        return new FileLogger(ids);
    }

    public void info(ResponseItem item) {
        // 打印比较好的日志
        String stream = item.getStream();
        if (stream != null) {
            buffer.append(stream);

            if (stream.endsWith("\n")) {
                String info = buffer.toString();
                String[] lines = info.split("\\n|\\r");
                for (String line : lines) {
                    if (line.startsWith("Step ")) {
                        this.info(line);
                    } else {
                        this.infoTab(line);
                    }
                }
                buffer.setLength(0);
            }

        } else if (item.getStatus() != null) {
            infoTab("{} {}", item.getStatus(), item.getProgress());
        } else {
            infoTab(item.toString());
        }

    }

    public void info(String format, Object... arguments) {
        String result = format;
        if (arguments.length != 0) {
            for (Object arg : arguments) {
                result = result.replaceFirst("\\{\\}", arg == null ? "" : String.valueOf(arg));
            }
        }
        writeLineToFile(result);
    }

    /**
     * 缩进一行
     *
     * @param format
     * @param arguments
     */
    public void infoTab(String format, Object... arguments) {
        this.info("    " + format, arguments);
    }


    private void writeLineToFile(String msg) {
        if (StringUtils.isBlank(msg)) {
            return;
        }
        log.info(msg);

        for (File logFile : fileDict.values()) {
            try {
                String time = DateFormatUtils.format(System.currentTimeMillis(), "HH:mm:ss");
                FileUtils.write(logFile, time + "  " + msg + "\r\n", StandardCharsets.UTF_8, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
