package com.gzqylc.da.service;

import com.gzqylc.da.dao.StarImageDao;
import com.gzqylc.da.entity.StarImage;
import com.gzqylc.lang.web.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StarImageService extends BaseService<StarImage> {

    @Autowired
    StarImageDao dao;

    public void star(String name) {
        StarImage db = dao.findByName(name);
        if (db == null) {
            db = new StarImage();
            db.setName(name);
            dao.save(db);
        }
    }


    public void unstar(String name) {
        StarImage db = dao.findByName(name);
        if (db != null) {
            dao.delete(db);
        }
    }
}
