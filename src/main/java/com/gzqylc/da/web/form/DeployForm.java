package com.gzqylc.da.web.form;

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
