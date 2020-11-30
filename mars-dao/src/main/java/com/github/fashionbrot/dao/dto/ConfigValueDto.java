package com.github.fashionbrot.dao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigValueDto {

    private String envCode;

    private String appName;

    private Integer updateReleaseStatus;

    private List<Integer> whereReleaseStatus;

}
