package com.gzqylc.da.entity;

import com.gzqylc.lang.web.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@FieldNameConstants
public class StarRepository extends BaseEntity {

    String name;



}
