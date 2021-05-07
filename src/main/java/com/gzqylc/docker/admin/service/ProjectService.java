package com.gzqylc.docker.admin.service;

import com.aliyuncs.exceptions.ClientException;
import com.gzqylc.docker.admin.web.RunnerHookController;
import com.gzqylc.docker.admin.web.logger.FileLogger;
import com.gzqylc.docker.admin.dao.*;
import com.gzqylc.docker.admin.entity.*;
import com.gzqylc.lang.web.JsonTool;
import com.gzqylc.lang.web.base.BaseService;
import com.gzqylc.lang.web.jpa.specification.Criteria;
import com.gzqylc.lang.web.jpa.specification.Restrictions;
import com.gzqylc.utils.HttpTool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
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
    RunnerService runnerService;

    @Autowired
    PipelineDao pipelineDao;

    @Autowired
    AppDao appDao;

    @Autowired
    ClassifyDao classifyDao;

    @Transactional
    public Project saveProject(Project project) {
        Registry registry = registryDao.findOne(project.getRegistry());

        project.setImageUrl(registry.getHost() + "/" + registry.getNamespace() + "/" + project.getName());
        if (project.getBuildConfig() == null) {
            App.BuildConfig buildConfig = new App.BuildConfig();


            project.setBuildConfig(buildConfig);
        }
        project = super.save(project);

        return project;
    }


    public Pipeline.PipeProcessResult buildImage(String pipelineId, String pipeId, Pipeline.PipeBuildConfig cfg) throws GitAPIException, InterruptedException, IOException {
        FileLogger logger = FileLogger.getLogger(pipelineId);
        logger.info("开始构建镜像任务开始");

        Runner runner = runnerService.getRunner();
        Assert.state(runner != null, "执行节点为空，请先配置");
        logger.info("执行节点信息, {}", runner);

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
        form.dockerfile = cfg.getDockerfile();
        form.logHook = cfg.getServerUrl() + RunnerHookController.API_LOG + "/" + pipelineId;
        form.resultHook = cfg.getServerUrl() + RunnerHookController.API_PIPE_FINISH + "/" + pipelineId + "/" + pipeId;

        logger.info("日志回调地址 (如果是localhost，构建阶段可能无法请求成功) {}", form.logHook);


        form.gitUrl = form.gitUrl.replace(runner.getGitUrlReplaceSource(), runner.getGitUrlReplaceTarget());
        logger.info("替换git地址 {}", form.gitUrl);


        String frpServer = frpService.getFrpServer();
        int vhostHttpPort = frpService.getVhostHttpPort();

        String postData = JsonTool.toJsonQuietly(form);
        String postUrl = "http://" + frpServer + ":" + vhostHttpPort + "/agent/build";

        logger.info("请求地址 {}", postUrl);
        logger.info("请求数据 {}", postData);
        String resp = HttpTool.postJson(runner.getHost().getDockerId(), postUrl, postData);


        logger.info("响应数据 {}", resp);


        return Pipeline.PipeProcessResult.PROCESSING;


    }

    @Transactional
    public void deleteProject(String id) throws ClientException {
        Project project = baseDao.findOne(id);

        // 判断是否有应用依赖

        Criteria<App> c = new Criteria<>();
        c.add(Restrictions.eq(App.Fields.imageUrl, project.getImageUrl()));
        long count = appDao.count(c);

        Assert.state(count == 0, "项目[" + project.getName() + "]有关联应用，请先删除相关应用");


        // 删除构建日志
        List<Pipeline> list = pipelineDao.findAllByField("project.id", id);
        pipelineDao.deleteAll(list);

        // 删除镜像仓库
        IRepositoryService repositoryService = RepositoryServiceFactory.getRepositoryService(project.getRegistry());

        try{
            repositoryService.deleteTag(project.getImageUrl(), project.getRegistry());
        }catch (Exception e){
            log.error("删除镜像仓库出错，请手动删除 {}", project.getImageUrl());
        }



        baseDao.deleteById(id);
    }

    public void updateRemarkAndClassify(Project project) {

        Project db = baseDao.findOne(project);
        Classify classify = classifyDao.findOne(project.getClassify().getId());
        //新增分组保存
        if(project.getGitUrl() != null){
            db.setGitUrl(project.getGitUrl());
        }

        db.setClassify(classify);
        db.setRemark(project.getRemark());
        baseDao.save(db);
    }


    @Data
    public static class BuildImageForm {
        String dockerfile;
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
