package com.github.fashionbrot.common.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataConfigReq {

    private String envCode;

    private String appId;

    private String version;

    private String token;
}
