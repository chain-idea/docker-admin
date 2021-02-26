package com.gzqylc.da.web;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Info;
import com.gzqylc.da.entity.Host;
import com.gzqylc.da.service.HostService;
import com.gzqylc.da.entity.Registry;
import com.gzqylc.da.web.vo.DockerInfo;
import com.gzqylc.framework.AjaxResult;
import com.gzqylc.framework.Route;
import com.gzqylc.lang.bean.Option;
import com.gzqylc.lang.web.RequestTool;
import com.gzqylc.lang.web.base.BaseController;
import com.gzqylc.da.service.docker.DockerTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@Route(value = "api/host")
public class HostController extends BaseController {


    @Autowired
    private HostService service;

    @Route("getScript")
    public AjaxResult add(HttpServletRequest request) throws IOException {
        String server = RequestTool.getBaseUrl(request);
        String uri = FrpController.INSTALL_FRPC;
        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }
        String scriptUrl = server + uri;
        String cmd = "curl -s " + scriptUrl + " | sh -e";

        return AjaxResult.success("获取命令成功", cmd);
    }

    @Route("notifyAdd/{dockerId}")
    public AjaxResult add(@PathVariable String dockerId) throws IOException {
        DockerClient client = DockerTool.getClient(dockerId);

        Info info = client.infoCmd().exec();

        Host host = new Host();
        host.setName(info.getName());
        host.setDockerId(dockerId);

        service.save(host);

        return AjaxResult.success("添加主机成功");
    }

    @Route("list")
    public Page<Host> list(@PageableDefault(sort = "name") Pageable pageable, Host host) {
        Page<Host> list = service.findAll(host, pageable);
        return list;
    }

    @Route("update")
    public AjaxResult update(@RequestBody Host host) {
        Assert.notNull(host.getId(), "id不能为空");
        service.updateNameAndRemark(host);
        return AjaxResult.success("保存");
    }


    @Route("delete")
    public AjaxResult delete(@RequestBody List<String> ids) {
        service.deleteAllById(ids);
        return AjaxResult.success("删除成功");
    }


    @Route("options")
    public List<Option> options(String searchText, String[] selected, Pageable pageable) {
        return service.findOptionList(searchText, selected, pageable, Registry.Fields.name, Host::getFullName);
    }

    @Route("get")
    public Map<String, Object> get(String id) {
        Host host = service.findOne(id);
        Info info = service.getDockerInfo(host);

        DockerInfo dockerInfo = new DockerInfo();
        BeanUtils.copyProperties(info, dockerInfo);



        Map<String, Object> result = new HashMap<>();
        result.put("host", host);
        result.put("info", dockerInfo);

        return result;
    }


    @Route("containers")
    public List<Container> containers(String id) {
        return service.getContainers(id);
    }

    @Route("images")
    public List<Image> images(String id) {
        return service.getImages(id);
    }

    @Route("deleteImage")
    public AjaxResult deleteImage(String id, String imageId) {
        try {
            service.deleteImage(id, imageId);
        } catch (ConflictException e) {
            return AjaxResult.error("删除镜像失败" + e.getMessage());
        }
        return AjaxResult.success();
    }

}
