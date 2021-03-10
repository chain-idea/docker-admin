package com.gzqylc.docker.admin.web;

import com.gzqylc.framework.AjaxResult;
import com.gzqylc.lang.web.jwt.Jwt;
import com.gzqylc.lang.web.jwt.JwtPayload;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录验证
 */
@RestController
@RequestMapping("api/login")
public class LoginController {


    @RequestMapping("/account")
    public AjaxResult ajaxLogin(@RequestBody Login login) {
        String username = login.username;
        String inputPassword = login.password;

        if (username.equals("admin") && inputPassword.equals("zc115200")) {
            JwtPayload payload = new JwtPayload();
            payload.setAud(username);
            String jwt = Jwt.createToken(payload);
            Map<String, Object> data = new HashMap<>();
            data.put("jwt", jwt);
            return AjaxResult.success("登录成功", data);
        }

        return AjaxResult.error("登录失败");
    }


    @Getter
    @Setter
    public static class Login {
        String username;
        String password;
    }

}
