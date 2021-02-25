package com.gzqylc.da.web;

import com.github.dockerjava.api.model.SearchItem;
import com.gzqylc.da.service.DockerHubService;
import com.gzqylc.lang.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/dockerHub")
public class DockerHubController extends BaseController {

    @GetMapping("list")
    public List<SearchItem> list(String keyword){

        return dockerHubService.search(keyword);
    }

    @Autowired
    DockerHubService dockerHubService;
}
