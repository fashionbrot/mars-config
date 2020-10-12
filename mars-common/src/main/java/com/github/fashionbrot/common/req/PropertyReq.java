package com.github.fashionbrot.common.req;

import lombok.Data;

@Data
public class PropertyReq extends PageReq {

    private String propertyKey;
    private String appName;
    private String templateKey;

}
