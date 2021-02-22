package com.gzqylc.da.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.PushImageCmd;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.collect.Sets;
import com.gzqylc.da.dao.HostDao;
import com.gzqylc.da.dao.RegistryDao;
import com.gzqylc.da.dao.RunnerDao;
import com.gzqylc.da.entity.*;
import com.gzqylc.da.web.logger.LogController;
import com.gzqylc.da.web.logger.PipelineLogger;
import com.gzqylc.da.service.docker.BuildImageResultCallback;
import com.gzqylc.da.service.docker.PushImageCallback;
import com.gzqylc.lang.web.JsonTool;
import com.gzqylc.lang.web.jpa.specification.Criteria;
import com.gzqylc.da.service.docker.DockerTool;
import com.gzqylc.lang.web.base.BaseService;
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


    public void buildImage(String pipelineId, Pipeline.PipeBuildConfig cfg) throws GitAPIException, InterruptedException, IOException {
        PipelineLogger logger = PipelineLogger.getLogger(pipelineId);
        logger.info("开始构建镜像任务开始");

        String dockerId = cfg.getBuildHostDockerId();
        if (dockerId == null) {
            logger.info("使用本机构建");
            // 获取代码
            File workDir = new File("/tmp/" + UUID.randomUUID());
            logger.info("工作目录为 {}", workDir.getAbsolutePath());
            logger.info("获取代码 git clone {}", cfg.getGitUrl());


            UsernamePasswordCredentialsProvider provider = new UsernamePasswordCredentialsProvider(cfg.getGitUsername(), cfg.getGitPassword());

            if (workDir.exists()) {
                boolean delete = workDir.delete();
                Assert.state(delete, "删除文件失败");
            }

            Git git = Git.cloneRepository()
                    .setURI(cfg.getGitUrl())
                    .setNoTags()
                    .setCredentialsProvider(provider)
                    .setDirectory(workDir)
                    .call();

            String commitMsg = git.log().call().iterator().next().getFullMessage();
            logger.info("git log : {}", commitMsg);
            git.close();


            logger.info("代码获取完毕, 共 {} M", FileUtils.sizeOfDirectory(workDir) / 1024 / 1024);

            logger.info("连接构建主机容器引擎中...");
            DockerClient dockerClient = DockerTool.getClient(dockerId, cfg.getRegistryHost(),
                    cfg.getRegistryUsername(),
                    cfg.getRegistryPassword());
            String imageUrl = cfg.getImageUrl();
            String latestTag = imageUrl + ":latest";
            String commitTag = imageUrl + ":" + cfg.getBranch();
            Set<String> tags = Sets.newHashSet(latestTag, commitTag);


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
            form.logUrl = cfg.getServerUrl() + LogController.API_LOG + "?id=" + pipelineId;


            String frpServer = frpService.getFrpServer();
            int vhostHttpPort = frpService.getVhostHttpPort();

            String cmd = JsonTool.toJsonQuietly(form);
            String runnerUrl = "http://" + frpServer + ":" + vhostHttpPort + "/agent/build";
            logger.info("发送指令 {}", runnerUrl);
            logger.info(cmd);
            HttpRequest request = HttpRequest.post(runnerUrl)
                    .header("Host", dockerId)
                    .send(cmd);
            String resp = request.body();
            logger.info("指令已发送 host {} {}", cmd, resp);
            int code = request.code();
            Assert.state(code == 200, "执行远程构建命令异常 " + resp);
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
        String logUrl;
    }
}
