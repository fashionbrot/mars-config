package com.github.fashionbrot.common.req;

import lombok.Data;

@Data
public class ConfigValueApiReq {

    private String appId;

    private String envCode;

    private Long version;

    private String templateKeys;

    private String all;
}
