package com.gzqylc.docker.admin.service;

import com.gzqylc.docker.admin.entity.GroupTarget;
import com.gzqylc.lang.web.base.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GroupTargetService extends BaseService<GroupTarget> {

    /**
     * 增删改查？
     */
    /**
     * 新增或者修改分组
     */
    public void saveOrUpdate(GroupTarget groupTarget){

        if(StringUtils.isEmpty(groupTarget.getId())){
            groupTarget.setCreateTime(new Date());
        }else{
            groupTarget.setModifyTime(new Date());
        }
        this.baseDao.save(groupTarget);
    }
}
