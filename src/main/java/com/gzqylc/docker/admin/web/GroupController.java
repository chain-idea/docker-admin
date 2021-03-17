package com.gzqylc.docker.admin.web;

import com.gzqylc.docker.admin.entity.Group;
import com.gzqylc.docker.admin.entity.GroupTarget;
import com.gzqylc.docker.admin.entity.Project;
import com.gzqylc.docker.admin.service.GroupService;
import com.gzqylc.docker.admin.service.GroupTargetService;
import com.gzqylc.framework.AjaxResult;
import com.gzqylc.framework.Route;
import com.gzqylc.lang.web.BeanTool;
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


/**
 * 分组管理
 */
@RestController
@Slf4j
@Route(value = "api/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupTargetService groupTargetService;

    /**
     *  查询所有的分组
     * @param keyword 关键字匹配
     * @param pageable 分页对象
     * @return
     */
    @Route("list")
    public Page<Group> list(String keyword, @PageableDefault(sort = BaseEntity.Fields.modifyTime, direction = Sort.Direction.DESC) Pageable pageable) {
        Criteria<Group> c = new Criteria<>();
        c.add(Restrictions.like(Group.Fields.groupName, keyword));

        Page<Group> list = groupService.findAll(c, pageable);
        return list;
    }

    /**
     * 添加或者保存分组菜单
     * @param group 分组对象 分组id为空为新增 分组id不为空则为修改
     * @return 通用返回对象
     */
    @Route("saveOrUpdateGroup")
    public AjaxResult saveOrUpdateGroup(@RequestBody Group group){

        groupService.saveOrUpdateGroup(group);
        return AjaxResult.success("操作成功");
    }

    /**
     * 根据id删除一个分组
     * @param id
     * @return
     */
    @Route("deleteGroupById")
    public AjaxResult deleteGroupById(String id){

        groupService.delete(id);
        return AjaxResult.success("操作成功");
    }

    /**
     * 分组目标表新增或者修改
     * @param groupTarget 分组目标对象
     * @return 通用返回
     */
    @Route("groupTatgetSaveOrUpdate")
    public AjaxResult groupTatgetSaveOrUpdate(@RequestBody GroupTarget groupTarget){

        groupTargetService.saveOrUpdate(groupTarget);
        return AjaxResult.success("操作成功");
    }

    /**
     * 根据id删除分组目标表条目
     * @param id 主键id
     * @return 通用返回对象
     */
    @Route("deleteGroupTargetById")
    public AjaxResult deleteGroupTargetById(String id){

        groupTargetService.delete(id);
        return AjaxResult.success("操作成功");
    }
}
