package com.github.fashionbrot.common.req;

import lombok.Data;

@Data
public class ConfigRecordReq extends PageReq {

    private String envCode;
    private String appName;
    private String templateKey;

    private Long id;
    private Integer operationType;

}
