package com.gzqylc.docker.admin.service;

import com.gzqylc.docker.admin.entity.Classify;
import com.gzqylc.lang.web.base.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ClassifyService extends BaseService<Classify> {


    /**
     * 增删改查？
     */
    /**
     * 新增或者修改分组
     */
    public void saveOrUpdateGroup(Classify classify){

        if (StringUtils.isEmpty(classify.getId())){
            classify.setCreateTime(new Date());
        }else{
            classify.setModifyTime(new Date());
        }
        this.baseDao.save(classify);
    }

}
