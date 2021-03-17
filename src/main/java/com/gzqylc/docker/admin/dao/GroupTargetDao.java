package com.gzqylc.docker.admin.dao;

import com.gzqylc.docker.admin.entity.GroupTarget;
import com.gzqylc.lang.web.base.BaseDao;
import org.springframework.stereotype.Repository;


@Repository
public class GroupTargetDao extends BaseDao<GroupTarget> {

     /**
      * 查询分组下面是否存在数据
      * @param groupId 分组id
      * @return true存在
      */
     boolean isExistsGroupTarget(String groupId){
          return this.findAllByField(GroupTarget.Fields.targetId,groupId).size() > 0 ;
     }
}
