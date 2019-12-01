package com.fashion.mars.spring.config;

import com.fashion.mars.spring.enums.ConfigTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarsDataConfig {

    private String envCode;

    private String appId;

    private String fileName;

    private String version;

    private String token;

    private String parentPath;

    private String content;

    /**
     * @see ConfigTypeEnum
     */
    private ConfigTypeEnum configType;
}
