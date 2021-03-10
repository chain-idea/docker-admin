//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gzqylc.docker.admin.web.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ContainerMountVo implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    String source;
    String destination;
    String driver;
    String mode;
    boolean rw;
    String propagation;
}
