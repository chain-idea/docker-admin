package com.gzqylc.docker.admin.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Info;
import com.gzqylc.docker.admin.entity.Host;
import com.gzqylc.lang.web.base.BaseService;
import com.gzqylc.docker.admin.service.docker.DockerTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HostService extends BaseService<Host> {


    public Info getDockerInfo(Host host) {

        DockerClient client = DockerTool.getClient(host.getDockerId());
        Info info = client.infoCmd().exec();

        return info;
    }


    public List<Container> getContainers(String id) {
        Host db = this.findOne(id);
        DockerClient client = DockerTool.getClient(db.getDockerId());

        List<Container> list = client.listContainersCmd().withShowAll(true).exec();
        return list;
    }

    public List<Image> getImages(String id) {
        Host db = this.findOne(id);
        DockerClient client = DockerTool.getClient(db.getDockerId());

        List<Image> list = client.listImagesCmd().withShowAll(true).exec();
        return list;
    }

    public void deleteImage(String id, String imageId) {
        Host db = this.findOne(id);
        DockerClient client = DockerTool.getClient(db.getDockerId());

        client.removeImageCmd(imageId).exec();

    }

    public void updateNameAndRemark(Host host) {
        Host db = baseDao.findOne(host);
        db.setName(host.getName());
        db.setRemark(host.getRemark());
        baseDao.save(db);
    }
}
