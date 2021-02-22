package com.gzqylc.da.web;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.gzqylc.da.entity.App;
import com.gzqylc.da.entity.Host;
import com.gzqylc.da.service.AppService;
import com.gzqylc.da.service.HostService;
import com.gzqylc.framework.AjaxResult;
import com.gzqylc.framework.Route;
import com.gzqylc.lang.web.base.BaseController;
import com.gzqylc.da.service.docker.DockerTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.nio.charset.Charset;

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

    @Route("logByHost")
    public void logByHost(@RequestParam String hostId, @RequestParam String containerId, HttpServletResponse response) throws Exception {
        Host host = hostService.findOne(hostId);

        DockerClient client = DockerTool.getClient(host.getDockerId());

        Charset charset = Charset.forName("iso-8859-1");


        PrintWriter out = response.getWriter();
        client.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .withTimestamps(false)
                .exec(new LogContainerResultCallback() {
                    @Override
                    public void onNext(Frame item) {
                        out.write(new String(item.getPayload(), charset));
                    }
                }).awaitCompletion();

    }


    @Route("log")
    public void log(String appId, String containerId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        App app = appService.findOne(appId);


        DockerClient client = DockerTool.getClient(app.getHost().getDockerId());

        Charset charset = Charset.forName("iso-8859-1");


        PrintWriter out = response.getWriter();
        client.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .withTimestamps(false)
                .exec(new LogContainerResultCallback() {
                    @Override
                    public void onNext(Frame item) {
                        out.write(new String(item.getPayload(), charset));
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

}
