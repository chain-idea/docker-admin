package com.gzqylc.da.web;

import com.github.dockerjava.api.model.Container;
import com.gzqylc.da.web.form.DeployForm;
import com.gzqylc.da.entity.App;
import com.gzqylc.da.service.AppService;
import com.gzqylc.da.service.HostService;
import com.gzqylc.da.web.vo.ContainerVo;
import com.gzqylc.framework.AjaxResult;
import com.gzqylc.framework.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Route(value = "api/app")
public class AppController {


    @Autowired
    private AppService service;

    @Autowired
    private HostService hostService;


    @Route("list")
    public Page<App> list(Pageable pageable, @RequestBody App app) {
        Page<App> list = service.findAll(app, pageable);
        return list;
    }


    @Route("delete")
    public AjaxResult delete(@RequestBody String id) {
        service.deleteApp(id);

        return AjaxResult.success("删除成功");
    }

    @Route("save")
    public AjaxResult save(@Valid @RequestBody DeployForm form) throws InterruptedException {
        App app = new App();
        String hostId = form.getHostId();
        app.setHost(hostService.findOne(hostId));
        app.setImageUrl(form.getImageUrl());
        app.setImageTag(form.getImageTag());
        app.setName(form.getAppName());

        App.AppConfig cfg = new App.AppConfig();
        cfg.setImage(form.getImageUrl() + ":" + form.getImageTag());

        app.setAutoDeploy(true);
        app.setConfig(cfg);

        app = service.save(app);

        service.deploy(app);

        AjaxResult rs = AjaxResult.success("创建成功");
        rs.put("id", app.getId());
        return rs;
    }


    @Route("updateConfig")
    public AjaxResult updateConfig(String id, @Valid @RequestBody App.AppConfig cfg) throws InterruptedException {

        App db = service.findOne(id);
        db.setConfig(cfg);

        service.save(db);


        service.deploy(db);

        AjaxResult rs = AjaxResult.success("更新成功");
        return rs;
    }


    @Route("autoDeploy")
    public AjaxResult autoDeploy(String id, boolean autoDeploy) throws InterruptedException {

        App db = service.findOne(id);
        db.setAutoDeploy(autoDeploy);

        service.save(db);


        AjaxResult rs = AjaxResult.success("自动部署:" + (autoDeploy ? "启用" : "停用"));
        return rs;
    }

    @Route("autoRestart")
    public AjaxResult autoRestart(String id, boolean autoRestart) throws InterruptedException {

        App db = service.findOne(id);
        db.setAutoRestart(autoRestart);
        db.getConfig().setRestart(autoRestart);

        service.save(db);
        service.deploy(db);


        AjaxResult rs = AjaxResult.success("自动重启:" + (autoRestart ? "启用" : "停用"));
        return rs;
    }


    @Route("deploy/{appId}")
    public AjaxResult deploy(@PathVariable String appId) throws InterruptedException {
        App app = service.findOne(appId);
        service.deploy(app);
        return AjaxResult.success("部署指令已发送");
    }


    @Route("start/{appId}")
    public AjaxResult start(@PathVariable String appId) {
        service.start(appId);
        return AjaxResult.success("部署指令已发送");
    }

    @Route("stop/{appId}")
    public AjaxResult stop(@PathVariable String appId) {
        service.stop(appId);
        return AjaxResult.success("部署指令已发送");
    }

    @Route("get")
    public App get(String id) {
        return service.findOne(id);
    }

    @Route("container")
    public ContainerVo containers(String id) {
        Container container = service.getContainer(id);
        return new ContainerVo(container);
    }

}
