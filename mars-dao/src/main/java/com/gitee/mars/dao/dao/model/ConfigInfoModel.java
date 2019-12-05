package com.gitee.mars.dao.dao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigInfoModel {

    private String envCode;

    private String appName;

    private List<String> templateKeyList;
}
