package com.gzqylc.docker.admin.web.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeployForm {
    String appName;

    @NotNull
    String hostId;
    String imageUrl;
    String imageTag;


}
