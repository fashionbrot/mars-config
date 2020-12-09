package com.github.fashionbrot.common.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigJsonVo {

    private List<JSONObject> jsonList;

    private String templateKey;

}
