package com.gzqylc.docker.admin.entity;


import com.gzqylc.lang.web.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * 分组表对象表 存储分组所对应的id
 */
@Entity
@Getter
@Setter
@FieldNameConstants
public class GroupTarget extends BaseEntity {

    /**
     * 分组名字的id
     */
    @NotNull
    String groupId;

    /**
     * 分组对象的id
     */
    @NotNull
    @Column(unique = true)
    String targetId;
}
