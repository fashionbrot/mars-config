package com.gitee.mars.common.req;

import com.gitee.mars.common.enums.ConfigTypeEnum;
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

    private String fileName;

    private String version;

    private String token;

    private String parentPath;

    /**
     * @see ConfigTypeEnum
     */
    private ConfigTypeEnum configType;
}
