package com.gzqylc.da.service;

import com.gzqylc.da.dao.PipelineJnlDao;
import com.gzqylc.da.entity.*;
import com.gzqylc.lang.web.JsonTool;
import com.gzqylc.lang.web.RequestTool;
import com.gzqylc.lang.web.base.BaseService;
import com.gzqylc.lang.web.spring.SpringTool;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class PipelineService extends BaseService<Pipeline> {

    @Autowired
    PipelineJnlDao dao;

    @Autowired
    AppService appService;

    @Autowired
    ProjectService projectService;

    @Autowired
    RegistryService registryService;

    public void trigger(Project project, String branch, HttpServletRequest request) throws GitAPIException, InterruptedException {
        // 添加上下文参数
        Pipeline pipeline = new Pipeline();
        pipeline.setProject(project);
        pipeline.setCommit(branch);

        String image = project.getImageUrl() + ":" + branch;


        List<Pipeline.PipeStage> stageList = pipeline.getStageList();

        int stageNumber = 1;
        int pipeNumber = 1;

        {
            Pipeline.PipeStage buildState = new Pipeline.PipeStage();
            buildState.setId("" + stageNumber);
            stageNumber++;
            buildState.setName("构建阶段");

            Pipeline.Pipe buildPipe = new Pipeline.Pipe();
            buildPipe.setId(buildState.getId() + "-" + pipeNumber);
            pipeNumber++;
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

            cfg.setServerUrl(RequestTool.getBaseUrl(request));

            buildPipe.setConfig(JsonTool.toJsonQuietly(cfg));


            buildState.getPipeList().add(buildPipe);
            stageList.add(buildState);


        }

        {
            List<App> apps = appService.findByImageUrl(project.getImageUrl());

            Pipeline.PipeStage deployStage = new Pipeline.PipeStage();
            deployStage.setId("" + stageNumber);
            stageNumber++;
            deployStage.setName("部署阶段");
            for (App a : apps) {
                if (a.getAutoDeploy()) {
                    Pipeline.Pipe deployPipe = new Pipeline.Pipe();
                    deployPipe.setId(deployPipe.getId() + "-" + pipeNumber);
                    pipeNumber++;

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

        pipeline.setStatus(Pipeline.Status.PENDING);


        // 保存
        pipeline = dao.save(pipeline);

        // 触发第一阶段
        Pipeline.PipeStage firstStage = stageList.get(0);
        for (Pipeline.Pipe pipe : firstStage.getPipeList()) {
            PipelineService service = SpringTool.getBean(getClass());
            service.handlePipeAsync(pipeline, pipe);
        }
    }

    @Async
    public void handlePipeAsync(Pipeline pipeline, Pipeline.Pipe pipe) {
        String pipelineId = pipeline.getId();
        List<Pipeline.PipeStage> stageList = pipeline.getStageList();

        // 修改状态
        Pipeline db = dao.findOne(pipelineId);
        db.setStatus(Pipeline.Status.PROCESSING);
        pipe.setStatus(Pipeline.Pipe.Status.PROCESSING);
        dao.save(db);


        Pipeline.PipeProcessResult result = Pipeline.PipeProcessResult.TODO;
        try {
            switch (pipe.getType()) {
                case BUILD_IMAGE: {
                    Pipeline.PipeBuildConfig cfg = JsonTool.jsonToBean(pipe.getConfig(), Pipeline.PipeBuildConfig.class);
                    result = projectService.buildImage(pipelineId, pipe.getId(),cfg);
                    break;
                }
                case DEPLOY: {
                    Pipeline.PipeDeployConfig cfg = JsonTool.jsonToBean(pipe.getConfig(), Pipeline.PipeDeployConfig.class);
                    result = appService.deploy(pipelineId, cfg.getName(), cfg.getHostname(), cfg.getImage(), cfg.getRegistryHost(),
                            cfg.getRegistryUsername(), cfg.getRegistryPassword(), cfg
                    );
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = Pipeline.PipeProcessResult.ERROR;
        }

        log.info("pipe执行结果{}", result);
        switch (result) {
            case TODO:
                break;
            case SUCCESS:
                notifyPipeFinish(pipeline, pipe, true);
                break;
            case PROCESSING:
                break;
            case ERROR:
                notifyPipeFinish(pipeline, pipe, false);
                break;
        }
    }

    @Async
    public void notifyPipeFinishAsync(String pipelineId, String pipeId, boolean success) {
        Pipeline pipeline = dao.findOne(pipelineId);

        Pipeline.Pipe pipe = null;
        for (Pipeline.PipeStage stage : pipeline.getStageList()) {
            for (Pipeline.Pipe p : stage.getPipeList()) {
                if (p.getId().equals(pipeId)) {
                    pipe = p;
                    break;
                }

            }
        }

        this.notifyPipeFinish(pipeline, pipe, success);

    }


    private void notifyPipeFinish(Pipeline pipeline, Pipeline.Pipe pipe, boolean success) {
        String pipelineId = pipeline.getId();
        List<Pipeline.PipeStage> stageList = pipeline.getStageList();

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
                    this.handlePipeAsync(pipeline, nextPipe);
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
