package com.gzqylc.da.web;

import com.gzqylc.da.entity.Host;
import com.gzqylc.da.service.FrpService;
import com.gzqylc.da.service.HostService;
import com.gzqylc.lang.web.RequestTool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController
@Slf4j
public class FrpController {


    public static final String INSTALL_FRPC = "/frp/install_frpc.sh";

    @Autowired
    FrpService frpService;


    @RequestMapping(INSTALL_FRPC)
    @ResponseBody
    public String frpcsh(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String base = RequestTool.getBaseUrl(request);
        if(base.endsWith("/")){
            base = base.substring(0, base.length() -1);
        }
        ClassPathResource res = new ClassPathResource("static/frp/install_frpc.sh");
        try (InputStream is = res.getInputStream()) {
            String sh = IOUtils.toString(is, StandardCharsets.UTF_8);

            sh = sh.replaceAll("\r","")
                    .replace("arg_web_server",base)

            ;


            response.addHeader("Content-Type", "application/octet-stream");

            return sh;
        }

    }


    @RequestMapping("/frp/{id}/frpc.ini")
    @ResponseBody
    public String frpcini(@PathVariable String id, HttpServletResponse response) throws IOException {
        log.info("客户端 ID : {}", id);
        String frpServer = frpService.getFrpServer();
        int frpPort = frpService.getFrpPort();


        ClassPathResource res = new ClassPathResource("static/frp/frpc.ini");
        try (InputStream is = res.getInputStream()) {
            String ini = IOUtils.toString(is, StandardCharsets.UTF_8);

            ini = ini.replace("127.0.0.1", frpServer)
                    .replace("7000", String.valueOf(frpPort))
                    .replace("id", id)
                    .replace("id", id);


            response.addHeader("Content-Type", "application/octet-stream");

            return ini;
        }

    }



}
