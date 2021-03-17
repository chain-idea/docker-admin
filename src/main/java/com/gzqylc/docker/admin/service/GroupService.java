package com.gzqylc.docker.admin.service;

import com.gzqylc.docker.admin.dao.GroupTargetDao;
import com.gzqylc.docker.admin.entity.Group;
import com.gzqylc.docker.admin.entity.GroupTarget;
import com.gzqylc.framework.exception.base.BaseException;
import com.gzqylc.lang.web.base.BaseEntity;
import com.gzqylc.lang.web.base.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GroupService extends BaseService<Group> {


    @Autowired
    private GroupTargetDao groupTargetDao;
    /**
     * 增删改查？
     */
    /**
     * 新增或者修改分组
     */
    public void saveOrUpdateGroup(Group group){

        if (StringUtils.isEmpty(group.getId())){
            group.setCreateTime(new Date());
        }else{
            group.setModifyTime(new Date());
        }
        this.baseDao.save(group);
    }

    /**
     * 根据id删除一个分组
     * @param id 分组id
     */
    public void deleteGroup(String id){
        //0 没找到常量池子
        //判断是否有
       if(groupTargetDao.findAllByField(GroupTarget.Fields.targetId,id).size() > 0){
        throw new BaseException("分组下面存在分组对象，无法删除");
       }
    }
}
