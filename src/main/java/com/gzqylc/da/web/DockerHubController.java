package com.gzqylc.da.web;

import com.gzqylc.da.entity.Repository;
import com.gzqylc.da.service.RepositoryServiceDockerHubImpl;
import com.gzqylc.lang.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/dockerHub")
public class DockerHubController extends BaseController {

    RepositoryServiceDockerHubImpl dockerHub = new RepositoryServiceDockerHubImpl();

    @GetMapping("list")
    public Page<Repository> list(String keyword) throws Exception {
        Page<Repository> list = dockerHub.findRepositoryList(null, null,keyword);
        return list;
    }

    @GetMapping("tagList")
    public List<String> tagList(String imageUrl) throws Exception {
        List<String> list = dockerHub.findTagList(imageUrl, null);

        return list;
    }

    @GetMapping("star")
    public List<String> star(String imageUrl) {
      //  List<String> list = dockerHub.star(imageUrl);


//        return list;
        return null;
    }


}
