package com.gzqylc.docker.admin.web;

import com.gzqylc.docker.admin.entity.Classify;
import com.gzqylc.docker.admin.entity.Registry;
import com.gzqylc.docker.admin.service.ClassifyService;
import com.gzqylc.framework.AjaxResult;
import com.gzqylc.framework.Route;
import com.gzqylc.lang.bean.Option;
import com.gzqylc.lang.web.base.BaseEntity;
import com.gzqylc.lang.web.jpa.specification.Criteria;
import com.gzqylc.lang.web.jpa.specification.Restrictions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 分组管理
 * @author yefeng
 */
@RestController
@Slf4j
@Route(value = "api/classify")
public class ClassifyController {

    @Autowired
    private ClassifyService classifyService;

    /**
     *  查询所有的分组
     * @param keyword 关键字匹配
     * @param pageable 分页对象
     * @return
     */
    @Route("list")
    public Page<Classify> list(String keyword, @PageableDefault(sort = BaseEntity.Fields.modifyTime, direction = Sort.Direction.DESC) Pageable pageable) {
        Criteria<Classify> c = new Criteria<>();
        c.add(Restrictions.like(Classify.Fields.name, keyword));

        Page<Classify> list = classifyService.findAll(c, pageable);
        return list;
    }

    /**
     * 添加或者保存分组菜单
     * @param classify 分组对象 分组id为空为新增 分组id不为空则为修改
     * @return 通用返回对象
     */
    @Route("saveOrUpdateClassify")
    public AjaxResult saveOrUpdateClassify(@RequestBody Classify classify){

        classifyService.saveOrUpdateClassify(classify);
        return AjaxResult.success("操作成功");
    }

    /**
     * 根据id删除一个分组
     * @param id
     * @return
     */
    @Route("deleteClassifyById")
    public AjaxResult deleteClassifyById(String id){
        classifyService.delete(id);
        return AjaxResult.success("操作成功");
    }

    /**
     * 获取全部的分组
     * @return 分组list
     */
    @Route("all")
    public List<Classify> all() {
        List<Classify> list = classifyService.findAll();
        return list;
    }

    @Route("options")
    public List<Option> options(String searchText, String[] selected, Pageable pageable) {
        return classifyService.findOptionList(searchText, selected, pageable, Registry.Fields.name, Classify::getName);
    }
}
