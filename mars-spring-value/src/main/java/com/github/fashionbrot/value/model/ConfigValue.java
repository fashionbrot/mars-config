package com.github.fashionbrot.value.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import java.util.List;

@Data
public class ConfigValue {

    private List<JSONObject> jsonList;

    private String templateKey;

}
