package com.gitee.mars.spring.api;

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
public class ForDataVo {

    private String appId;

    private String envCode;

    private String fileName;

    private String content;

    private String version;

}
