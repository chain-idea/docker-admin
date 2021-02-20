package com.gzqylc.da.service;

import com.gzqylc.da.dao.PipelineJnlDao;
import com.gzqylc.da.entity.*;
import com.gzqylc.lang.web.JsonTool;
import com.gzqylc.lang.web.base.BaseService;
import com.gzqylc.lang.web.spring.SpringTool;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PipelineService extends BaseService<Pipeline> {

    @Autowired
    PipelineJnlDao dao;

    @Autowired
    AppService appService;

    @Autowired
    ProjectService projectService;

    @Autowired
    RegistryService registryService;

    public void trigger(Project project, String branch) throws GitAPIException, InterruptedException {
        // 添加上下文参数
        Pipeline jnl = new Pipeline();
        jnl.setProject(project);
        jnl.setCommit(branch);

        String image = project.getImageUrl() + ":" + branch;


        List<Pipeline.PipeStage> stageList = jnl.getStageList();

        {
            Pipeline.PipeStage buildState = new Pipeline.PipeStage();
            buildState.setName("构建阶段");
            Pipeline.Pipe buildPipe = new Pipeline.Pipe();
            buildPipe.setName("标准构建");
            buildPipe.setType(Pipeline.Pipe.Type.BUILD_IMAGE);

            App.BuildConfig buildConfig = project.getBuildConfig();
            Pipeline.PipeBuildConfig cfg = new Pipeline.PipeBuildConfig();
            BeanUtils.copyProperties(buildConfig, cfg);

            cfg.setBranch(branch);

            cfg.setImageUrl(project.getImageUrl());

            cfg.setGitUrl(project.getGitUrl());
            cfg.setGitUsername(project.getGitUsername());
            cfg.setGitPassword(project.getGitPassword());

            cfg.setRegistryHost(project.getRegistry().getHost());
            cfg.setRegistryUsername(project.getRegistry().getUsername());
            cfg.setRegistryPassword(project.getRegistry().getPassword());


            buildPipe.setConfig(JsonTool.toJsonQuietly(cfg));


            buildState.getPipeList().add(buildPipe);
            stageList.add(buildState);
        }

        {
            List<App> apps = appService.findByImageUrl(project.getImageUrl());

            Pipeline.PipeStage deployStage = new Pipeline.PipeStage();
            deployStage.setName("部署阶段");
            for (App a : apps) {
                if (a.getAutoDeploy()) {
                    Pipeline.Pipe deployPipe = new Pipeline.Pipe();
                    deployPipe.setType(Pipeline.Pipe.Type.DEPLOY);
                    deployPipe.setName("部署应用");

                    Pipeline.PipeDeployConfig cfg = new Pipeline.PipeDeployConfig();
                    BeanUtils.copyProperties(a.getConfig(), cfg);

                    cfg.setName(a.getName());
                    cfg.setHostname(a.getHost().getDockerId());

                    Registry registry = registryService.findByUrl(a.getImageUrl());

                    cfg.setRegistryHost(registry.getHost());
                    cfg.setRegistryUsername(registry.getUsername());
                    cfg.setRegistryPassword(registry.getPassword());

                    cfg.setImage(image);

                    deployPipe.setConfig(JsonTool.toJsonQuietly(cfg));

                    deployStage.getPipeList().add(deployPipe);
                }
            }

            if (!deployStage.getPipeList().isEmpty()) {
                stageList.add(deployStage);
            }
        }

        jnl.setStatus(Pipeline.Status.PENDING);


        // 保存
        jnl = dao.save(jnl);

        // 触发第一阶段
        Pipeline.PipeStage firstStage = stageList.get(0);
        for (Pipeline.Pipe pipe : firstStage.getPipeList()) {
            SpringTool.getBean(getClass()).handlePipe(jnl.getId(), jnl.getStageList(), pipe);
        }
    }

    @Async
    public void handlePipe(String pipelineId, List<Pipeline.PipeStage> stageList, Pipeline.Pipe pipe) {
        Pipeline db = dao.findOne(pipelineId);
        db.setStatus(Pipeline.Status.PROCESSING);
        pipe.setStatus(Pipeline.Pipe.Status.PROCESSING);
        db.setStageList(stageList);
        dao.save(db);

        try {
            switch (pipe.getType()) {
                case BUILD_IMAGE: {
                    Pipeline.PipeBuildConfig cfg = JsonTool.jsonToBean(pipe.getConfig(), Pipeline.PipeBuildConfig.class);
                    projectService.buildImage(pipelineId, cfg);
                    break;
                }
                case DEPLOY: {
                    Pipeline.PipeDeployConfig cfg = JsonTool.jsonToBean(pipe.getConfig(), Pipeline.PipeDeployConfig.class);
                    appService.deploy(pipelineId, cfg.getName(), cfg.getHostname(), cfg.getImage(), cfg.getRegistryHost(),
                            cfg.getRegistryUsername(), cfg.getRegistryPassword(), cfg
                    );
                    break;
                }
            }
            notifyPipeFinish(pipelineId, stageList, pipe, true);
        } catch (Exception e) {
            e.printStackTrace();
            notifyPipeFinish(pipelineId, stageList, pipe, false);
        }

    }

    private void notifyPipeFinish(String pipelineId, List<Pipeline.PipeStage> stageList, Pipeline.Pipe pipe, boolean success) {
        pipe.setStatus(success ? Pipeline.Pipe.Status.SUCCESS : Pipeline.Pipe.Status.ERROR);

        Pipeline db = dao.findOne(pipelineId);
        db.setStageList(stageList);
        dao.save(db);


        Pipeline.PipeStage currentStage = stageList.stream().filter(pipeStage -> pipeStage.getPipeList().contains(pipe)).findFirst().get();
        currentStage.setFinishCount(currentStage.getFinishCount() + 1);

        // 本阶段完成
        if (currentStage.getFinishCount() == currentStage.getPipeList().size() && currentStage.getErrorCount() == 0) {
            // 下一个任务
            int nextStageIndex = stageList.indexOf(currentStage) + 1;

            if (nextStageIndex == stageList.size()) {
                // 所有阶段完成

                notifyStop(pipelineId);

            } else if (nextStageIndex < stageList.size()) {
                Pipeline.PipeStage nextStage = stageList.get(nextStageIndex);
                for (Pipeline.Pipe nextPipe : nextStage.getPipeList()) {
                    this.handlePipe(pipelineId, stageList, nextPipe);
                }
            }
        }
    }

    private void notifyStop(String pipelineId) {
        Pipeline db = dao.findOne(pipelineId);
        db.setStopTime(new Date());
        db.setConsumeTime((int) ((System.currentTimeMillis() - db.getCreateTime().getTime()) / 1000));
        db.setStatus(Pipeline.Status.SUCCESS);
        dao.save(db);
    }


}
