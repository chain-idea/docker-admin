package com.gzqylc.docker.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gzqylc.lang.web.base.BaseEntity;
import com.gzqylc.lang.web.jpa.converter.ObjectConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@FieldNameConstants
public class Project extends BaseEntity {

    @NotNull
    @Column(unique = true)
    String name;

    String remark;


    String gitUrl;
    String gitUsername;

    String gitPassword;

    @NotNull
    String imageUrl;


    @NotNull
    @ManyToOne
    Registry registry;

    @JsonIgnore
    @Lob
    @Convert(converter = ObjectConverter.class)
    App.BuildConfig buildConfig;

    @ManyToOne
    Classify classify = new Classify(Classify.DEFAULT_GROUP_ID);
}
