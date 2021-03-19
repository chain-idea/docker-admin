package com.gzqylc.docker.admin.service.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.gzqylc.lang.tool.SystemTool;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DockerTool {

    private static String FRP_VHOST;

    public static void setFrpVHost(String frpVHost) {
        FRP_VHOST = frpVHost;
    }

    public static String getFrpVhost() {
        return FRP_VHOST;
    }

    public static DockerClient getClient(String dockerId) {
        return getClient(dockerId, null, null, null);
    }

    public static DockerClient getClient(String dockerId, String registryUrl, String registryUsername, String registryPassword) {
        String dockerHost = dockerId == null ? getLocalDockerHost() : FRP_VHOST;
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .withRegistryUsername(registryUsername)
                .withRegistryPassword(registryPassword)
                .withRegistryUrl(registryUrl)
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .virtualHost(dockerId) // 使用dockerId 作为路由转发的标识
                .build();
        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

        return dockerClient;
    }


    private static String getLocalDockerHost() {
        return SystemTool.isWindows() ? "tcp://localhost:2375" : "unix:///var/run/docker.sock";
    }


    public static Map<String, String> getAppLabelFilter(String name) {
        Map<String, String> labels = new HashMap<>();
        labels.put("app.name", name);
        return labels;
    }




}
