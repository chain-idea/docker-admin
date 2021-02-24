package com.gzqylc.da.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.PushImageCmd;
import com.google.common.collect.Sets;
import com.gzqylc.da.dao.HostDao;
import com.gzqylc.da.dao.RegistryDao;
import com.gzqylc.da.dao.RunnerDao;
import com.gzqylc.da.entity.*;
import com.gzqylc.da.web.RunnerHookController;
import com.gzqylc.da.web.logger.PipelineLogger;
import com.gzqylc.da.service.docker.BuildImageResultCallback;
import com.gzqylc.da.service.docker.PushImageCallback;
import com.gzqylc.lang.web.JsonTool;
import com.gzqylc.lang.web.jpa.specification.Criteria;
import com.gzqylc.da.service.docker.DockerTool;
import com.gzqylc.lang.web.base.BaseService;
import com.gzqylc.utils.GitTool;
import com.gzqylc.utils.HttpTool;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ProjectService extends BaseService<Project> {


    @Autowired
    RegistryDao registryDao;

    @Autowired
    HostDao hostDao;

    @Autowired
    RunnerDao runnerDao;

    @Autowired
    FrpService frpService;

    @Autowired
    RunnerDao dao;

    public void saveProject(Project project) {
        Registry registry = registryDao.findOne(project.getRegistry());

        project.setImageUrl(registry.getHost() + "/" + registry.getNamespace() + "/" + project.getName());
        if (project.getBuildConfig() == null) {
            App.BuildConfig buildConfig = new App.BuildConfig();
            Runner config = runnerDao.findTop1(new Criteria<>(), Sort.by("seq"));
            if (config != null) {
                buildConfig.setBuildHost(config.getHost().getId());
                buildConfig.setBuildHostDockerId(config.getHost().getDockerId());
            }

            project.setBuildConfig(buildConfig);
        }

        project = super.save(project);
    }


    public Pipeline.PipeProcessResult buildImage(String pipelineId, String pipeId, Pipeline.PipeBuildConfig cfg) throws GitAPIException, InterruptedException, IOException {
        PipelineLogger logger = PipelineLogger.getLogger(pipelineId);
        logger.info("开始构建镜像任务开始");

        String dockerId = cfg.getBuildHostDockerId();
        if (dockerId == null) {
            logger.info("使用本机构建");
            // 获取代码
            File workDir = new File("/tmp/" + UUID.randomUUID());
            GitTool.clone(cfg.getGitUrl(), cfg.getGitUsername(), cfg.getGitPassword(), cfg.getBranch(), workDir);


            logger.info("连接构建主机容器引擎中...");
            DockerClient dockerClient = DockerTool.getClient(dockerId, cfg.getRegistryHost(),
                    cfg.getRegistryUsername(),
                    cfg.getRegistryPassword());
            String imageUrl = cfg.getImageUrl();
            String commitTag = imageUrl + ":" + cfg.getBranch();
            Set<String> tags = Sets.newHashSet(commitTag);


            File buildDir = new File(workDir, cfg.getContext());


            BuildImageCmd buildImageCmd = dockerClient.buildImageCmd(buildDir).withTags(tags);
            boolean useCache = cfg.isUseCache();
            logger.info("是否使用缓存  {}", useCache);
            buildImageCmd.withNoCache(!useCache);

            logger.info("向docker发送构建指令");
            String imageId = buildImageCmd.exec(new BuildImageResultCallback(logger)).awaitImageId();
            logger.info("镜像构建结束 imageId={}", imageId);

            // 推送
            logger.info("推送镜像");
            for (String tag : tags) {
                PushImageCmd pushImageCmd = dockerClient.pushImageCmd(tag);
                pushImageCmd.exec(new PushImageCallback(logger)).awaitCompletion();
            }

            dockerClient.close();
            logger.info("构建阶段结束");
            return Pipeline.PipeProcessResult.SUCCESS;
        } else {


            logger.info("使用远程机器构建, 构建主机Id {}. dockerId:{}", cfg.getBuildHost(), cfg.getBuildHostDockerId());

            BuildImageForm form = new BuildImageForm();
            form.gitUrl = cfg.getGitUrl();
            form.gitUsername = cfg.getGitUsername();
            form.gitPassword = cfg.getGitPassword();
            form.branch = cfg.getBranch();
            form.regHost = cfg.getRegistryHost();
            form.regUsername = cfg.getRegistryUsername();
            form.regPassword = cfg.getRegistryPassword();
            form.imageUrl = cfg.getImageUrl();
            form.buildContext = cfg.getContext();
            form.logHook = cfg.getServerUrl() + RunnerHookController.API_LOG + "/" + pipelineId;
            form.resultHook = cfg.getServerUrl() + RunnerHookController.API_PIPE_FINISH + "/" + pipelineId + "/" + pipeId;

            Runner runner = runnerDao.findByDockerId(dockerId);
            if (runner != null) {
                form.gitUrl = form.gitUrl.replace(runner.getGitUrlReplaceSource(), runner.getGitUrlReplaceTarget());
                logger.info("替换git地址 {}", form.gitUrl);
            }


            String frpServer = frpService.getFrpServer();
            int vhostHttpPort = frpService.getVhostHttpPort();

            String postData = JsonTool.toJsonQuietly(form);
            String postUrl = "http://" + frpServer + ":" + vhostHttpPort + "/agent/build";

            logger.info("请求地址 {}", postUrl);
            logger.info("请求数据 {}", postData);
            String resp = HttpTool.postJson(dockerId, postUrl, postData);


            logger.info("响应数据 {}", resp);

            return Pipeline.PipeProcessResult.PROCESSING;
        }


    }


    @Data
    public static class BuildImageForm {
        String gitUrl;
        String gitUsername;
        String gitPassword;
        String branch;
        String regHost;
        String regUsername;
        String regPassword;
        String imageUrl;
        String buildContext;
        String logHook;
        String resultHook;
    }
}
