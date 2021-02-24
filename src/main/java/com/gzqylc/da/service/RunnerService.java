package com.gzqylc.da.service;

import com.gzqylc.da.dao.RunnerDao;
import com.gzqylc.da.entity.Runner;
import com.gzqylc.lang.web.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class RunnerService extends BaseService<Runner> {


    /**
     * 获得执行器
     *
     * @return
     */
    public Runner getRunner() {
        // TODO 负载均衡
        List<Runner> all = dao.findAll();
        Assert.notEmpty(all, "请先配置执行器");

        return all.get(0);
    }


    @Autowired
    RunnerDao dao;


}
