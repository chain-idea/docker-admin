package com.gzqylc;

import com.gzqylc.docker.admin.entity.Host;
import com.gzqylc.docker.admin.service.FrpService;
import com.gzqylc.docker.admin.service.HostService;
import com.gzqylc.docker.admin.service.docker.DockerTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 启动后执行
 */
@Component
@Slf4j
public class DataInit implements ApplicationRunner {


    @Autowired
    FrpService frpService;

    @Autowired
    HostService hostService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String frpServer = frpService.getFrpServer();
        int vhostHttpPort = frpService.getVhostHttpPort();
        String frpWeb = "tcp://" + frpServer + ":" + vhostHttpPort;
        DockerTool.setFrpVHost(frpWeb);

        // 将宿主机也作为主机
        Host host = hostService.findOne("-1");

        if (host == null) {
            host = new Host();
            host.setId("-1");
            host.setName("本机");
            hostService.save(host);
        }
    }
}
