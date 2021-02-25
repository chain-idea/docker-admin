package com.gzqylc.da.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.SearchItem;
import com.github.kevinsawicki.http.HttpRequest;
import com.gzqylc.da.service.docker.DockerTool;
import com.gzqylc.lang.web.JsonTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DockerHubService {

    public List<SearchItem> search(String keyword) {
        if (keyword == null) {
            return new ArrayList<>();
        }
        DockerClient client = DockerTool.getClient(null);
        List<SearchItem> exec = client.searchImagesCmd(keyword).exec();

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return exec;
    }


    public List<String> tagList(String imageUrl) {
        String url = "https://registry.hub.docker.com/v1/repositories/" + imageUrl + "/tags";
        String body = HttpRequest.get(url).body();

        List<Map> maps = JsonTool.jsonToBeanListQuietly(body, Map.class);


        List<String> tagList = maps.stream().map(map -> (String) map.get("name")).collect(Collectors.toList());

        log.info("{}", maps);
        return tagList;

    }


}
