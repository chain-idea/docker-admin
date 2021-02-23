package com.gzqylc.da.web;

import com.gzqylc.da.web.logger.PipelineLogger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController

public class RunnerMsgReceiveController {

    public static final String API_LOG = "api/runner/log";

    @RequestMapping(API_LOG + "/{id}")
    public void log(@PathVariable String id, @RequestBody String msg) throws IOException {
        PipelineLogger logger = PipelineLogger.getLogger(id);
        logger.info(msg);
    }



}
