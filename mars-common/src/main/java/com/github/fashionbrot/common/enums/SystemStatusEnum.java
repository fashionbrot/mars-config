package com.github.fashionbrot.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SystemStatusEnum {


    ADD(1,"新增"),
    UPDATE(2,"更新"),
    DELETE(3,"删除"),
    RELEASE(4,"已发布")
    ;

    private int code;
    private String desc;
}
