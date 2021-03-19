package com.gzqylc.docker.admin.web;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.gzqylc.docker.admin.entity.Host;
import com.gzqylc.docker.admin.service.AppService;
import com.gzqylc.docker.admin.service.HostService;
import com.gzqylc.framework.AjaxResult;
import com.gzqylc.framework.Route;
import com.gzqylc.lang.web.base.BaseController;
import com.gzqylc.docker.admin.service.docker.DockerTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.print.Doc;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@Route(value = "api/container")
public class ContainerController extends BaseController {

    @Autowired
    AppService appService;

    @Autowired
    HostService hostService;


    @Route("get")
    @ResponseBody
    public InspectContainerResponse get(@RequestParam String hostId, String containerId) throws Exception {
        Host host = hostService.findOne(hostId);
        DockerClient client = DockerTool.getClient(host.getDockerId());

        InspectContainerResponse response = client.inspectContainerCmd(containerId).exec();


        return response;
    }

    @Route("log/{hostId}/{containerId}")
    public void logByHost(@PathVariable String hostId, @PathVariable String containerId, HttpServletResponse response) throws Exception {
        Host host = hostService.findOne(hostId);

        DockerClient client = DockerTool.getClient(host.getDockerId());

        Charset charset = Charset.forName("iso-8859-1");


        PrintWriter out = response.getWriter();
        client.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .withTimestamps(false)
                .withFollowStream(true)
                .withTail(500)
                .exec(new LogContainerResultCallback() {
                    @Override
                    public void onNext(Frame item) {
                        out.write(new String(item.getPayload(), charset));
                        out.flush();
                    }
                }).awaitCompletion();

    }


    @Route("remove")
    @ResponseBody
    public AjaxResult removeContainer(String hostId, String containerId) {
        Host host = hostService.findOne(hostId);
        DockerClient client = DockerTool.getClient(host.getDockerId());

        client.removeContainerCmd(containerId)
                .exec();

        return AjaxResult.success("删除容器成功");

    }

    @Route("stop")
    @ResponseBody
    public AjaxResult stop(String hostId, String containerId) {

        Host host = hostService.findOne(hostId);


        DockerClient client = DockerTool.getClient(host.getDockerId());


        client.stopContainerCmd(containerId)
                .exec();


        return AjaxResult.success("停止容器成功");

    }

    @Route("start")
    @ResponseBody
    public AjaxResult start(String hostId, String containerId) {

        Host host = hostService.findOne(hostId);


        DockerClient client = DockerTool.getClient(host.getDockerId());


        client.startContainerCmd(containerId)
                .exec();


        return AjaxResult.success("启动容器成功");

    }


    @Route("status")
    @ResponseBody
    public AjaxResult status(String hostId, String appName) {
        Host host = hostService.findOne(hostId);
        DockerClient client = DockerTool.getClient(host.getDockerId());
        Map<String, String> appLabelFilter = DockerTool.getAppLabelFilter(appName);

        try {

            List<Container> list = client.listContainersCmd().withLabelFilter(appLabelFilter).withShowAll(true).exec();
            if (list.isEmpty()) {
                return AjaxResult.success("未知");
            }

            Container container = list.get(0);

            return AjaxResult.success(container.getStatus());
        } catch (NotFoundException e) {

            return AjaxResult.success("未知");
        }

    }
}
