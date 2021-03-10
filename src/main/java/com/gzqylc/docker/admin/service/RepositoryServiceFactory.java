package com.gzqylc.docker.admin.service;

import com.gzqylc.docker.admin.entity.Registry;

public class RepositoryServiceFactory {


    public static IRepositoryService getRepositoryService(Registry registry) {
        String url = registry.getHost();
        return getRepositoryService(url);
    }


    public static IRepositoryService getRepositoryService(String url) {
        if (url.contains("aliyuncs.com")) {
            return new RepositoryServiceAliyunImpl();
        }

        // 目前只实现了阿里云镜像仓库接口
        return new RepositoryServiceDockerHubImpl();
    }


}
