package com.gzqylc.da.web;

import com.github.dockerjava.api.model.SearchItem;
import com.gzqylc.da.service.DockerHubService;
import com.gzqylc.lang.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/dockerHub")
public class DockerHubController extends BaseController {

    @GetMapping("list")
    public PageImpl<SearchItem> list(String keyword) {
        List<SearchItem> list = dockerHubService.search(keyword);
        return new PageImpl<>(list);
    }

    @GetMapping("tagList")
    public List<String> tagList(String imageUrl) {
        List<String> list = dockerHubService.tagList(imageUrl);

        return list;
    }

    @Autowired
    DockerHubService dockerHubService;
}
