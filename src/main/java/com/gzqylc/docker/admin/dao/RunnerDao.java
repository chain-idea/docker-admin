package com.gzqylc.docker.admin.dao;

import com.gzqylc.docker.admin.entity.Host;
import com.gzqylc.docker.admin.entity.Runner;
import com.gzqylc.lang.web.base.BaseDao;
import com.gzqylc.lang.web.jpa.specification.Criteria;
import com.gzqylc.lang.web.jpa.specification.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class RunnerDao extends BaseDao<Runner> {

    public Runner findByDockerId(String dockerId) {
        Criteria<Runner> c = new Criteria<>();
        c.add(Restrictions.eq(Runner.Fields.host + "." + Host.Fields.dockerId, dockerId));

        Runner runner = this.findOne(c);

        return runner;
    }
}
