package com.gzqylc.da.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.gzqylc.da.dao.HostDao;
import com.gzqylc.da.entity.App;
import com.gzqylc.da.entity.Host;
import com.gzqylc.da.entity.Pipeline;
import com.gzqylc.da.entity.Registry;
import com.gzqylc.da.service.docker.PullImageCallback;
import com.gzqylc.lang.web.base.BaseService;
import com.gzqylc.lang.web.jpa.specification.Criteria;
import com.gzqylc.lang.web.jpa.specification.Restrictions;
import com.gzqylc.da.web.logger.PipelineLogger;
import com.gzqylc.da.service.docker.DockerTool;
import com.gzqylc.lang.web.spring.SpringTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AppService extends BaseService<App> {


    @Autowired
    RegistryService registryService;

    @Autowired
    HostDao hostDao;


    @Async
    public void deploy(App app) throws InterruptedException {
        Registry registry = registryService.findByUrl(app.getImageUrl());

        String dockerId = app.getHost().getDockerId();
        String image = app.getImageUrl() + ":" + app.getImageTag();

        String registryHost = registry.getHost();
        String registryUsername = registry.getUsername();
        String registryPassword = registry.getPassword();
        String name = app.getName();

        App.AppConfig cfg = app.getConfig();


        this.deploy(null, name, dockerId, image, registryHost, registryUsername, registryPassword, cfg);
    }


    public Pipeline.PipeProcessResult deploy(String pipelineId, String name, String dockerId, String image,
                                             String registryHost, String registryUsername, String registryPassword,
                                             App.AppConfig cfg) throws InterruptedException {
        PipelineLogger logger = PipelineLogger.getLogger(pipelineId);
        logger.info("部署阶段开始");
        DockerClient client = DockerTool.getClient(dockerId, registryHost, registryUsername, registryPassword);

        logger.info("开始拉取镜像");
        client.pullImageCmd(image).exec(new PullImageCallback(logger)).awaitCompletion();


        logger.info("开始部署镜像 {}", image);

        List<Container> containers = getContainer(name, client);


        for (Container container : containers) {

            logger.info("容器状态 {}", container.getState());
            if (container.getState().equals("running")) {
                logger.info("停止容器{}", container.getNames());
                client.stopContainerCmd(container.getId()).exec();
            }
            logger.info("删除容器{}", container.getNames());
            client.removeContainerCmd(container.getId()).exec();
        }


        HostConfig hostConfig = new HostConfig();

        // 端口
        Ports ports = new Ports();
        List<ExposedPort> exposedPorts = new ArrayList<>();
        for (App.PortBinding p : cfg.getPorts()) {
            ExposedPort e = new ExposedPort(p.getPrivatePort(), InternetProtocol.valueOf(p.getProtocol()));
            exposedPorts.add(e);

            ports.bind(e, Ports.Binding.bindPort(p.getPublicPort()));
        }
        hostConfig.withPortBindings(ports);


        // 路径绑定
        List<Bind> binds = new ArrayList<>();
        for (App.BindConfig v : cfg.getBinds()) {
            // /host:/container:ro
            AccessMode accessMode = v.getReadOnly() ? AccessMode.ro : AccessMode.rw;
            binds.add(new Bind(v.getPublicVolume(), new Volume(v.getPrivateVolume()), accessMode));
        }
        hostConfig.withBinds(binds);


        // 环境变量
        List<String> envs = new ArrayList<>();
        for (App.EnvironmentConfig envCfg : cfg.getEnvironment()) {
            envs.add(envCfg.getKey() + "=" + envCfg.getValue());
        }

        // 是否自动启动
        if (cfg.isRestart()) {
            hostConfig.withRestartPolicy(RestartPolicy.alwaysRestart());
        }


        logger.info("主机配置{}", hostConfig.getBinds());
        CreateContainerResponse response = client.createContainerCmd(image)
                .withName(name + "_1")
                .withLabels(getAppLabelFilter(name))
                .withHostConfig(hostConfig)
                .withExposedPorts(exposedPorts) // 如果dockerfile中未指定端口，需要在这里指定
                .withEnv(envs)
                .exec();


        logger.info("创建容器{}", response);

        client.startContainerCmd(response.getId()).exec();

        logger.info("启动容器");
        logger.info("部署阶段结束");

        return Pipeline.PipeProcessResult.SUCCESS;
    }

    public void stop(String id) {
        App app = this.findOne(id);

        String hostname = app.getHost().getDockerId();

        DockerClient client = DockerTool.getClient(hostname);

        List<Container> list = getContainer(app.getName(), client);

        for (Container container : list) {
            client.stopContainerCmd(container.getId()).exec();
        }
    }


    public void start(String id) {
        App app = this.findOne(id);
        String hostname = app.getHost().getDockerId();

        DockerClient client = DockerTool.getClient(hostname);

        List<Container> list = getContainer(app.getName(), client);

        for (Container container : list) {
            client.startContainerCmd(container.getId()).exec();
        }
    }

    private List<Container> getContainer(String name, DockerClient client) {
        Map<String, String> labels = getAppLabelFilter(name);
        List<Container> list = client.listContainersCmd().withLabelFilter(labels).withShowAll(true).exec();
        return list;
    }

    private Map<String, String> getAppLabelFilter(String name) {
        Map<String, String> labels = new HashMap<>();
        labels.put("app.name", name);
        return labels;
    }

    public Container getContainer(String id) {
        App app = this.findOne(id);
        String dockerId = app.getHost().getDockerId();
        DockerClient client = DockerTool.getClient(dockerId);

        String name = app.getName();

        Map<String, String> labels = getAppLabelFilter(name);
        List<Container> list = client.listContainersCmd().withLabelFilter(labels).withShowAll(true).exec();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public List<App> findByImageUrl(String imageUrl) {
        Criteria<App> c = new Criteria<>();
        c.add(Restrictions.eq(App.Fields.imageUrl, imageUrl));

        return findAll(c);
    }


    public App updatePorts(String id, List<App.PortBinding> ports) {
        App db = findOne(id);
        if (db.getConfig() == null) {
            db.setConfig(new App.AppConfig());
        }
        db.getConfig().setPorts(ports);
        return save(db);

    }

    public void deleteApp(String id) {
        // 远程删除应用
        App app = baseDao.findOne(id);
        deleteContainer(app);

        baseDao.deleteById(id);
    }


    public void moveApp(String id, String hostId) throws InterruptedException {
        Assert.hasLength(hostId, "hostId不能为空");
        // 远程删除应用
        App app = baseDao.findOne(id);
        this.deleteContainer(app);


        Host targetHost = hostDao.findOne(hostId);
        app.setHost(targetHost);
        save(app);
        SpringTool.getBean(getClass()).deploy(app);

    }

    public void updateApp(String id, String tag) throws InterruptedException {
        Assert.hasLength(tag, "tag不能为空");
        // 远程删除应用
        App app = baseDao.findOne(id);
        this.deleteContainer(app);

        app.setImageTag(tag);
        save(app);

        SpringTool.getBean(getClass()).deploy(app);
    }

    private void deleteContainer(App app) {
        DockerClient client = DockerTool.getClient(app.getHost().getDockerId());


        Map<String, String> labels = getAppLabelFilter(app.getName());
        List<Container> list = client.listContainersCmd().withLabelFilter(labels).withShowAll(true).exec();
        log.info("已有容器个数 {}", list.size());

        list.forEach(c -> {
            if (c.getState().equals("running")) {
                log.info("正在停止容器 {}", c);
                client.stopContainerCmd(c.getId()).exec();
            }
            log.info("正在删除容器 {}", c);
            client.removeContainerCmd(c.getId()).exec();
        });
    }


}
