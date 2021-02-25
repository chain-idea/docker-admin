package com.gzqylc.da.service;

import com.aliyuncs.exceptions.ClientException;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.SearchItem;
import com.github.kevinsawicki.http.HttpRequest;
import com.gzqylc.da.entity.Registry;
import com.gzqylc.da.entity.Repository;
import com.gzqylc.da.service.docker.DockerTool;
import com.gzqylc.lang.web.JsonTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// registry.cn-hangzhou.aliyuncs.com
@Slf4j
public class RepositoryServiceDockerHubImpl implements IRepositoryService {


    @Override
    public Page<Repository> findRepositoryList(Pageable pageable, Registry registry, String keyword) throws Exception {
        if (keyword == null) {
            return Page.empty();
        }
        DockerClient client = DockerTool.getClient(null);
        List<SearchItem> list = client.searchImagesCmd(keyword).exec();

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Repository> resultList = list.stream().map(i -> {
            Repository r = new Repository();
            r.setName(i.getName());
            r.setDescription(i.getDescription());
            r.setStarCount(i.getStarCount());
            r.setOfficial(i.isOfficial());
            return r;
        }).collect(Collectors.toList());

        return new PageImpl<>(resultList);
    }

    @Override
    public List<String> findTagList(String url, Registry registry) throws Exception {
        String api = "https://registry.hub.docker.com/v1/repositories/" + url + "/tags";
        String body = HttpRequest.get(api).body();

        List<Map> maps = JsonTool.jsonToBeanListQuietly(body, Map.class);


        List<String> tagList = maps.stream().map(map -> (String) map.get("name")).collect(Collectors.toList());

        return tagList;

    }

    @Override
    public void deleteTag(String imageUrl, Registry registry) throws ClientException {
        Assert.state(false, "不支持删除");
    }


}
