package com.gzqylc.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.gzqylc.lang.tool.SystemTool;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DockerTool {

    private static String DOCKER_HOST_ROUTER;

    public static void setFrpWeb(String frpWeb) {
        DOCKER_HOST_ROUTER = frpWeb;
    }

    public static DockerClient getLocalClient(String url, String username, String password) {
        String localDockerHost = getLocalDockerHost();
        log.info("本机dockerHost {}", localDockerHost);
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(localDockerHost)
                .withRegistryUsername(username)
                .withRegistryPassword(password)
                .withRegistryUrl(url)
                .build();


        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();
        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);
        return dockerClient;
    }


    private static String getLocalDockerHost() {
        if (SystemTool.isWindows()) {
            return "tcp://localhost:2375";
        }
        return "unix:///var/run/docker.sock";
    }

    public static DockerClient getClient(String dockerId) {
        return getClient(dockerId, null, null, null);
    }


    /**
     * @param dockerId abc.test.com
     */
    public static DockerClient getClient(String dockerId, String url, String username, String password) {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(DOCKER_HOST_ROUTER)
                .withRegistryUsername(username)
                .withRegistryPassword(password)
                .withRegistryUrl(url)
                .build();


        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .virtualHost(dockerId)
                .build();
        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);
        return dockerClient;
    }




}
