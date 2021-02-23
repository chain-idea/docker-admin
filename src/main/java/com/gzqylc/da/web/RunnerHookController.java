package com.gzqylc.da.web;

import com.gzqylc.da.service.PipelineService;
import com.gzqylc.da.web.logger.PipelineLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController

public class RunnerHookController {

    public static final String API_LOG = "api/runner_hook/log";
    public static final String API_EVENT = "api/runner_hook/event";


    @RequestMapping(API_LOG + "/{id}")
    public void log(@PathVariable String id, @RequestBody String msg) throws IOException {
        PipelineLogger logger = PipelineLogger.getLogger(id);
        logger.info(msg);
    }

    @RequestMapping(API_EVENT + "/{id}")
    public void hook(@PathVariable String id, String event) throws IOException {
        PipelineLogger logger = PipelineLogger.getLogger(id);
        logger.info("接受到事件 {}" + event);

    }


    @Autowired
    private PipelineService pipelineService;
}
