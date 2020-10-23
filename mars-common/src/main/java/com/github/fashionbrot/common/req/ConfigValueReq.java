package com.github.fashionbrot.common.req;

import lombok.Data;

@Data
public class ConfigValueReq extends PageReq {


    private String envCode;
    private String appName;
    private String templateKey;
    private String description;

    private String tableName;
}
