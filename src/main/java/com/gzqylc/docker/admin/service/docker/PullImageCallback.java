package com.gzqylc.docker.admin.service.docker;

import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.model.PullResponseItem;
import com.gzqylc.docker.admin.web.logger.FileLogger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PullImageCallback extends ResultCallbackTemplate<PullImageCallback, PullResponseItem> {

    private FileLogger logger;
    private String error;

    public PullImageCallback(FileLogger logger) {
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
