package com.gzqylc.docker.admin.web;

import com.aliyuncs.exceptions.ClientException;
import com.gzqylc.docker.admin.entity.App;
import com.gzqylc.docker.admin.service.AppService;
import com.gzqylc.docker.admin.entity.Project;
import com.gzqylc.docker.admin.service.ProjectService;
import com.gzqylc.lang.web.base.BaseEntity;
import com.gzqylc.lang.web.jpa.specification.Criteria;
import com.gzqylc.lang.web.jpa.specification.Restrictions;
import com.gzqylc.framework.AjaxResult;
import com.gzqylc.framework.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@Route(value = "api/project")
public class ProjectController {

    @Autowired
    private ProjectService service;

    @Autowired
    private AppService appService;

    @Route("list")
    public Page<Project> list(String keyword, @PageableDefault(sort = BaseEntity.Fields.modifyTime, direction = Sort.Direction.DESC) Pageable pageable) {
        Criteria<Project> c = new Criteria<>();
        c.add(Restrictions.like(Project.Fields.name, keyword));

        Page<Project> list = service.findAll(c, pageable);
        return list;
    }

    @Route("save")
    public AjaxResult save(@RequestBody Project project) {
        Project db = service.saveProject(project);

        AjaxResult rs = AjaxResult.success("保存成功");
        rs.put("id", db.getId());
        return rs;
    }

    @Route("update")
    public AjaxResult update(@RequestBody Project project) {
        service.updateRemark(project);
        return AjaxResult.success("修改成功");
    }

    @Route(value = "delete", title = "删除项目" , logEnable = true)
    public AjaxResult delete(@RequestBody String id) throws ClientException {
        service.deleteProject(id);
        return AjaxResult.success("删除成功");
    }

    @Route("get")
    public Project get(String id) {
        return service.findOne(id);
    }


    @Route("updateBuild")
    public AjaxResult updateBuild(String projectId, @RequestBody App.BuildConfig cfg) {
        Project db = service.findOne(projectId);
        db.setBuildConfig(cfg);

        service.save(db);
        return AjaxResult.success("修改成功");
    }


    @Route("getBuild")
    public App.BuildConfig getBuild(@RequestParam String projectId) {
        Project db = service.findOne(projectId);

        return db.getBuildConfig();
    }

    @Route("apps")
    public List<App> apps(String id) {
        Project project = service.findOne(id);
        return appService.findByImageUrl(project.getImageUrl());
    }


}
