package com.github.fashionbrot.common.enums;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum  DateEnum {

    YEAR("year",null),
    TIME("time",null),
    DATE("date",null),
    DATETIME("datetime",DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    ;


    private String dataType;
    private DateTimeFormatter pattern;

    private static Map<String, DateEnum> all = Maps.newHashMap();

    static {

        Arrays.stream(DateEnum.values()).forEach(map->{
            all.put(map.getDataType(),map);
        });
    }

    public static DateEnum ofDateType(String dataType){
        if (all.containsKey(dataType)){
            return all.get(dataType);
        }
        return null;
    }
}
