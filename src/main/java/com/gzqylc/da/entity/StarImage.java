package com.gzqylc.da.entity;

import com.gzqylc.lang.web.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@FieldNameConstants
public class StarImage extends BaseEntity {

    @Column(unique = true)
    String name;



}
