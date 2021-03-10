package com.gzqylc.docker.admin.entity;

import com.gzqylc.lang.web.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@FieldNameConstants
public class Registry extends BaseEntity {

    @NotNull
    @Column(unique = true)
    String name;

    @NotNull
    String username;
    @NotNull
    String password;
    @NotNull
    String host;
    @NotNull
    String namespace;


    String aliyunAccessKeySecret;
    String aliyunAccessKeyId;

}
