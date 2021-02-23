package com.gzqylc.da.web.logger;

import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController

public class LogController {

    public static final String API_LOG = "api/log";

    @RequestMapping(API_LOG)
    public void log(String id, @RequestBody byte[] body) throws IOException {
        String msg = new String(body, StandardCharsets.UTF_8);


        PipelineLogger logger = PipelineLogger.getLogger(id);
        logger.info(msg);
    }
}
