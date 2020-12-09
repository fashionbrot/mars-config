package com.github.fashionbrot.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiRespVo{

    private int code;
    private String msg;
    private Object data;
    private Long version;
}
