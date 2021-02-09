package com.gzqylc.da.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Tag {

    Date modifyTime;
    Date createTime;
    Long size;
    String tag;
}
