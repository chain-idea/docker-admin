package com.gzqylc.da.service.pipe.callback;

import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.model.PushResponseItem;
import com.gzqylc.da.web.logger.PipelineLogger;

public class PushImageCallback extends ResultCallbackTemplate<PushImageCallback, PushResponseItem> {

    private PipelineLogger logger;
    private String error;

    public PushImageCallback(PipelineLogger logger) {
        this.logger = logger;
    }

    @Override
    public void onNext(PushResponseItem item) {
        if (item.isErrorIndicated()) {
            this.error = item.getError();
        }
        logger.info(item);
    }

    public String getError() {
        return error;
    }
}
