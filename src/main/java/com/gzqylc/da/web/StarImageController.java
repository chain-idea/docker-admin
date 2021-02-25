package com.gzqylc.da.web;

import com.gzqylc.da.entity.StarImage;
import com.gzqylc.da.service.StarImageService;
import com.gzqylc.framework.AjaxResult;
import com.gzqylc.framework.Route;
import com.gzqylc.lang.web.base.BaseController;
import com.gzqylc.lang.web.base.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/starImage")
public class StarImageController extends BaseController {

    @Autowired
    private StarImageService service;

    @Route("list")
    public Page<StarImage> list(@PageableDefault(sort = BaseEntity.Fields.modifyTime, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<StarImage> list = service.findAll(pageable);
        return list;
    }


    @RequestMapping("star/{name}")
    public AjaxResult star(@PathVariable String name) {
        service.star(name);

        return AjaxResult.success("收藏成功");
    }

    @RequestMapping("unstar/{name}")
    public AjaxResult unstar(@PathVariable String name) {
        service.unstar(name);

        return AjaxResult.success("收藏成功");
    }
}
