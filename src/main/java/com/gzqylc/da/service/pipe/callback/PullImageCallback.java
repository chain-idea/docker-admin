package com.gzqylc.da.service.pipe.callback;

import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.model.PullResponseItem;
import com.gzqylc.da.web.logger.PipelineLogger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PullImageCallback extends ResultCallbackTemplate<PullImageCallback, PullResponseItem> {

    private PipelineLogger logger;
    private String error;

    public PullImageCallback(PipelineLogger logger) {
        this.logger = logger;
    }

    @Override
    public void onNext(PullResponseItem item) {
        if (item.isErrorIndicated()) {
            this.error = item.getError();
        }
        if (logger != null) {
            logger.info(item);
        }else {
            log.info(item.toString());
        }

    }

    public String getError() {
        return error;
    }
}
