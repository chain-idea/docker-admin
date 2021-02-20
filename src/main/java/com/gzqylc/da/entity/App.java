package com.gzqylc.da.entity;

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

    @Lob
    @Convert(converter = ObjectConverter.class)
    AppConfig config;

    @Data
    public static class AppConfig {

        String image;
        boolean privileged;

        String restart; // always, no

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

    @Data
    public static class BindConfig {
        String publicVolume;
        String privateVolume;
        Boolean readOnly; // ro, rw

    }

    @Data
    public static class EnvironmentConfig {
        String key;
        String value;
    }

    @Data
    public static class PortBinding {
        Integer publicPort;
        Integer privatePort;
        String protocol;
    }

    @Data
    public static class BuildConfig {
        String context = "/";
        String dockerfile = "Dockerfile";

        boolean autoBuild = false;
        boolean useCache = true;

        String buildHost; // 构建镜像的主机ID
    }
}
