package com.gzqylc.da.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gzqylc.lang.web.base.BaseEntity;
import com.gzqylc.lang.web.jpa.converter.ObjectConverter;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用
 * <p>
 * 注：应用和项目无直接关系
 */
@Getter
@Setter
@Entity
@FieldNameConstants
public class App extends BaseEntity {

    @NotNull
    @Column(unique = true)
    String name;


    @NotNull
    @ManyToOne
    Host host;

    @NotNull
    String imageUrl;

    @NotNull
    String imageTag;


    // 自动部署
    @NotNull
    Boolean autoDeploy;

    // 自动重启
    @NotNull
    Boolean autoRestart;

    @Lob
    @Convert(converter = ObjectConverter.class)
    AppConfig config;

    @Transient
    String logUrl;



    @Data
    public static class AppConfig {

        String image;
        boolean privileged;

        boolean restart; // always, no

        // 主机:容器
        List<PortBinding> ports = new ArrayList<>(); //  - 7100:7100/udp  - 7100:7100/tcp

        /**
         * /var/run/docker.sock:/var/run/docker.sock:ro
         * /var/run/docker.sock:/var/run/docker.sock:rw
         */
        List<BindConfig> binds = new ArrayList<>();


        // a=b
        List<EnvironmentConfig> environment = new ArrayList<>();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class BindConfig {
        String publicVolume;
        String privateVolume;
        Boolean readOnly; // ro, rw

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class EnvironmentConfig {
        String key;
        String value;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class PortBinding {
        Integer publicPort;
        Integer privatePort;
        String protocol;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class BuildConfig {
        String context = "/";
        String dockerfile = "Dockerfile";

        boolean useCache = true;


    }
}
