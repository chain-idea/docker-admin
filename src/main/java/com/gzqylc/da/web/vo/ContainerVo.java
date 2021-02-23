
package com.gzqylc.da.web.vo;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class ContainerVo implements Serializable {
    private static final long serialVersionUID = 1L;

    public ContainerVo(Container c) {
        if (c == null) {
            return;
        }
        image = c.getImage();
        name = c.getNames()[0].substring(1);
        state = c.getState();
        id = c.getId();
    }

    String id;
    String image;
    String name;

    String state;

}
