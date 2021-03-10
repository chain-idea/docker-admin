package com.gzqylc.docker.admin.dao;

import com.gzqylc.docker.admin.entity.StarImage;
import com.gzqylc.lang.web.base.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public class StarImageDao extends BaseDao<StarImage> {

    public StarImage findByName(String name) {
        return this.findOneByField(StarImage.Fields.name, name);
    }
}
