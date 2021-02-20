package com.gzqylc.da.entity;

import com.gzqylc.lang.web.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * 指定构建主机
 */
@Getter
@Setter
@Entity
public class RunnerHostConfig extends BaseEntity {

    @NotNull
    @ManyToOne
    Host host;

    int seq;

    // 局域网的git地址，替换公网地址，加快速度
    String gitUrlReplace;


}
