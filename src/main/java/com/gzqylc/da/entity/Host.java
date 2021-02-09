package com.gzqylc.da.entity;

import com.gzqylc.lang.web.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 主机信息
 */
@Entity
@Getter
@Setter
public class Host extends BaseEntity {

    String name;


    String dockerId;

}
