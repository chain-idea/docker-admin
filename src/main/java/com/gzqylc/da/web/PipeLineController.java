package com.gzqylc.da.web;

import com.gzqylc.da.entity.Pipeline;
import com.gzqylc.da.entity.Project;
import com.gzqylc.da.service.PipelineService;
import com.gzqylc.da.service.ProjectService;
import com.gzqylc.framework.AjaxResult;
import com.gzqylc.framework.Route;
import com.gzqylc.lang.web.base.BaseEntity;
import com.gzqylc.lang.web.jpa.specification.Criteria;
import com.gzqylc.lang.web.jpa.specification.Restrictions;
import com.gzqylc.da.web.logger.LoggerConstants;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api/pipeline")
public class PipeLineController {

    @Autowired
    PipelineService service;

    @Autowired
    ProjectService projectService;


    @RequestMapping("list")
    public Page<Pipeline> list(@RequestParam String projectId, @PageableDefault(sort = BaseEntity.Fields.createTime, direction = Sort.Direction.DESC) Pageable pageable, HttpServletRequest request) {
        String prefix = "ws://" + request.getServerName() + ":" + request.getServerPort() + LoggerConstants.WEBSOCKET_URL_PREFIX;
        Criteria<Pipeline> c = new Criteria<>();
        c.add(Restrictions.eq(Pipeline.Fields.project +".id", projectId));
        Page<Pipeline> list = service.findAll(c,pageable);
        list.forEach(p -> p.setLogUrl(prefix + p.getId()));
        return list;
    }

    @RequestMapping("trigger")
    public AjaxResult trigger(String id, String type, String value) throws GitAPIException, InterruptedException {
        Project p = projectService.findOne(id);
        service.trigger(p, value);

        return AjaxResult.success("流水线触发成功");
    }

    @Route("delete")
    public AjaxResult delete(@RequestBody List<String> ids) {
        service.deleteAllById(ids);
        return AjaxResult.success("删除成功");
    }

}
