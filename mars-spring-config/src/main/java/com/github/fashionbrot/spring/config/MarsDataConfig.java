package com.github.fashionbrot.spring.config;

import com.github.fashionbrot.spring.enums.ConfigTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
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
