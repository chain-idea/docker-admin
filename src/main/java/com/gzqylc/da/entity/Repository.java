package com.gzqylc.da.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Repository {
    String name;
    String type;
    String summary;

    String url;

    Date modifyTime;
    String latestVersion;

    String description;
    int starCount;

    boolean isOfficial;


}
