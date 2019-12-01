package com.fashion.mars.spring.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
