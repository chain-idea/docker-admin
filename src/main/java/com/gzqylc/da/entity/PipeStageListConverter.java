package com.gzqylc.da.entity;

import com.gzqylc.lang.web.JsonTool;

import javax.persistence.AttributeConverter;
import java.util.List;

public class PipeStageListConverter implements AttributeConverter<List<Pipeline.PipeStage>, String> {
    @Override
    public String convertToDatabaseColumn(List<Pipeline.PipeStage> list) {
        return JsonTool.toJsonQuietly(list);
    }

    @Override
    public List<Pipeline.PipeStage> convertToEntityAttribute(String s) {
        return JsonTool.jsonToBeanListQuietly(s, Pipeline.PipeStage.class);
    }
}
