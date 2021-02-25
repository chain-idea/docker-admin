package com.gzqylc.da.service;

import com.gzqylc.da.dao.RegistryDao;
import com.gzqylc.da.entity.Registry;
import com.gzqylc.lang.web.base.BaseService;
import com.gzqylc.lang.web.jpa.specification.Criteria;
import com.gzqylc.lang.web.jpa.specification.Restrictions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RegistryService extends BaseService<Registry> {

    @Autowired
    RegistryDao dao;


    public Registry findByUrl(String url) {
        if (!url.contains("/")) {
            log.info("{} 不包含斜杠，应是官方镜像", url);
            return null;
        }

        Criteria<Registry> c = new Criteria<>();


        String[] arr = url.split("/");

        c.add(Restrictions.eq(Registry.Fields.host, arr[0]));
        c.add(Restrictions.eq(Registry.Fields.namespace, arr[1]));

        Registry r = dao.findOne(c);


        return r;
    }


}
