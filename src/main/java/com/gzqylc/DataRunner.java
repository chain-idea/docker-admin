package com.gzqylc;

import com.gzqylc.da.service.FrpService;
import com.gzqylc.utils.DockerTool;
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
public class DataRunner implements ApplicationRunner {


    @Autowired
    FrpService frpService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String frpServer = frpService.getFrpServer();
        int vhostHttpPort = frpService.getVhostHttpPort();
        String frpWeb = "tcp://" + frpServer + ":" + vhostHttpPort;
        DockerTool.setFrpWeb(frpWeb);


    }
}
