package com.gzqylc.da.service;

import com.gzqylc.da.entity.Registry;

public class RepositoryServiceFactory {


    public static IRepositoryService getRepositoryService(Registry registry) {
        String url = registry.getHost();
        return getRepositoryService(url);
    }


    public static RepositoryServiceAliyunImpl getRepositoryService(String url) {
        if (url.contains("aliyuncs.com")) {
            return new RepositoryServiceAliyunImpl();
        }

        // 目前只实现了阿里云镜像仓库接口
        return null;
    }


}
