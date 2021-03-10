package com.gzqylc.docker.admin.web;

import com.gzqylc.docker.admin.dao.RunnerDao;
import com.gzqylc.docker.admin.entity.Runner;
import com.gzqylc.framework.AjaxResult;
import com.gzqylc.framework.Route;
import com.gzqylc.lang.web.base.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@Route(value = "api/runner")
public class RunnerController extends BaseController {

    @Autowired
    private RunnerDao service;

    @Route("list")
    public Page<Runner> list(@PageableDefault(sort = Runner.Fields.seq) Pageable pageable) {
        Page<Runner> list = service.findAll(pageable);
        return list;
    }

    @Route("all")
    public List<Runner> all() {
        List<Runner> list = service.findAll();
        return list;
    }

    @Route("save")
    public AjaxResult save(@RequestBody Runner t) {
        service.save(t);

        return AjaxResult.success("保存成功");
    }

    @Route("update")
    public AjaxResult update(@RequestBody Runner t) {
        service.save(t);
        return AjaxResult.success("修改成功");
    }

    @Route("delete")
    public AjaxResult delete(@RequestBody List<String> ids) {
        service.deleteAllById(ids);
        return AjaxResult.success("删除成功");
    }


}