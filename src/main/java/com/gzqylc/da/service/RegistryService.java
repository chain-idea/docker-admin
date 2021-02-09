package com.gzqylc.da.service;

import com.gzqylc.da.dao.RegistryDao;
import com.gzqylc.da.entity.Registry;
import com.gzqylc.lang.web.base.BaseService;
import com.gzqylc.lang.web.jpa.specification.Criteria;
import com.gzqylc.lang.web.jpa.specification.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistryService extends BaseService<Registry> {

    @Autowired
    RegistryDao dao;


    public Registry findByUrl(String url) {
        Criteria<Registry> c = new Criteria<>();

        String[] arr = url.split("/");

        c.add(Restrictions.eq(Registry.Fields.host, arr[0]));
        c.add(Restrictions.eq(Registry.Fields.namespace, arr[1]));

        Registry r = dao.findOne(c);


        return r;
    }


}
