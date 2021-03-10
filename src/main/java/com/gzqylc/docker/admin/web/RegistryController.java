package com.gzqylc.docker.admin.web;

import com.gzqylc.docker.admin.entity.Registry;
import com.gzqylc.docker.admin.service.RegistryService;
import com.gzqylc.framework.AjaxResult;
import com.gzqylc.framework.Route;
import com.gzqylc.lang.bean.Option;
import com.gzqylc.lang.web.base.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@Route(value = "api/registry")
public class RegistryController extends BaseController {

    @Autowired
    private RegistryService service;

    @Route("list")
    public Page<Registry> list(Pageable pageable, Registry t) {
        Page<Registry> list = service.findAll(t, pageable);
        return list;
    }

    @Route("all")
    public List<Registry> all() {
        List<Registry> list = service.findAll();
        return list;
    }

    @Route("save")
    public AjaxResult save(@RequestBody Registry t) {
        service.save(t);

        return AjaxResult.success("保存成功");
    }

    @Route("update")
    public AjaxResult update(@RequestBody Registry t) {
        service.save(t);
        return AjaxResult.success("修改成功");
    }

    @Route("delete")
    public AjaxResult delete(@RequestBody List<String> ids) {
        service.deleteAllById(ids);
        return AjaxResult.success("删除成功");
    }

    @Route("options")
    public List<Option> options(String searchText, String[] selected, Pageable pageable) {
        return service.findOptionList(searchText, selected, pageable, Registry.Fields.name, Registry::getName);
    }

}