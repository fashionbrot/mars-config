package com.github.fashionbrot.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigValueVo {

    private List<JsonVo> jsonList;

    private String templateKey;

}
