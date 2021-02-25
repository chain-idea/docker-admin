package com.gzqylc.da.web;

import com.gzqylc.da.dao.RunnerDao;
import com.gzqylc.da.entity.Runner;
import com.gzqylc.da.entity.StarRepository;
import com.gzqylc.da.service.StarRepositoryService;
import com.gzqylc.framework.Route;
import com.gzqylc.lang.web.base.BaseController;
import com.gzqylc.lang.web.base.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/starRepository")
public class StarRepositoryController extends BaseController {

    @Autowired
    private StarRepositoryService service;

    @Route("list")
    public Page<StarRepository> list(@PageableDefault(sort = BaseEntity.Fields.modifyTime, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<StarRepository> list = service.findAll(pageable);
        return list;
    }


    @RequestMapping("save")
    public void save(String name) {

    }




}
