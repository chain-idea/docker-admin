package com.gzqylc.da.web.logger;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class LogController {

    public static final String API_LOG = "api/log";

    @RequestMapping(API_LOG)
    public void log(String id, @RequestBody String body) {
        PipelineLogger logger = PipelineLogger.getLogger(id);
        logger.info(body);
    }
}
