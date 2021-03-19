package com.gzqylc.docker.admin.entity;

import com.gzqylc.lang.web.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;

/**
 * 主机信息
 */
@Entity
@Getter
@Setter
@ToString
@FieldNameConstants
public class Host extends BaseEntity {

    String name;
    String remark;


    public String getFullName() {
        return name + (remark != null ? " (" + remark + ")" : "");
    }


    @Column(unique = true)
    String dockerId;

    @ManyToOne
    Classify classify;

    @Transient
    String classifyId;
}
