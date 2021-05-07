package com.gzqylc.docker.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gzqylc.lang.web.base.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 执行记录
 */
@Entity
@Getter
@Setter
@FieldNameConstants
public class Pipeline extends BaseEntity {


    @JsonIgnore
    @NotNull
    @ManyToOne
    Project project;


    String commit;
    String commitMessage;


    @Lob
    @Convert(converter = PipeStageListConverter.class)
    List<PipeStage> stageList = new ArrayList<>();

    String logUrl;

    @NotNull
    @Enumerated
    Status status;


    Date stopTime;

    /**
     * 耗时， 单位秒
     */
    Integer consumeTime;


    public enum Status {
        PENDING,
        PROCESSING,
        SUCCESS,
        ERROR
    }

    @Data
    public static class PipeBuildConfig extends App.BuildConfig {
        String imageUrl;

        String branch;
        String gitUrl;
        String gitUsername;
        String gitPassword;

        String registryHost;

        String registryUsername;
        String registryPassword;

        String serverUrl; // 网页访问的地址
    }

    @Data
    public static class PipeDeployConfig extends App.AppConfig {

        String name;

        String image;
        String hostname;

        String registryHost;

        String registryUsername;
        String registryPassword;
        String appId;
    }

    @Data
    public static class PipeStage {

        String id;
        String name;

        List<Pipe> pipeList = new ArrayList<>();


        int finishCount;
        int errorCount;


    }

    @Data
    public static class Pipe {


        String id;
        String name;
        String config;


        Type type;


        Status status = Status.PENDING;

        public enum Type {
            BUILD_IMAGE,
            DEPLOY
        }

        public enum Status {
            PENDING,
            PROCESSING,
            SUCCESS,
            ERROR,
            CANCEL
        }
    }

    public static enum PipeProcessResult {
        TODO,

        PROCESSING, // 异步
        SUCCESS,
        ERROR
    }
}
