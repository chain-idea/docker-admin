package com.gzqylc.da.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.SearchItem;
import com.gzqylc.da.service.docker.DockerTool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DockerHubService {

    public List<SearchItem> search(String keyword) {
        DockerClient client = DockerTool.getClient(null);
        List<SearchItem> exec = client.searchImagesCmd(keyword).exec();

        return exec;
    }
}
