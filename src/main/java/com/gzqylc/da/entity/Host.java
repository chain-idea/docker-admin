package com.gzqylc.da.entity;

import com.gzqylc.lang.web.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * 主机信息
 */
@Entity
@Getter
@Setter
@ToString
public class Host extends BaseEntity {

    String name;


    @Column(unique = true)
    String dockerId;

}
