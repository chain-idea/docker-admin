package com.gzqylc.da.service;

import org.springframework.stereotype.Service;

@Service
public class FrpService {

    public String getFrpServer() {
        return "39.105.74.223";
    }

    public int getFrpPort() {
        return 7700;
    }

    public int getVhostHttpPort() {
        return 7780;
    }


}
