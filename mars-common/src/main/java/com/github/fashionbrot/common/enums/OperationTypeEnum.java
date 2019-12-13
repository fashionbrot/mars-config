package com.github.fashionbrot.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperationTypeEnum {

    ADD(1,"新增"),
    UPDATE(2,"修改"),
    DELETE(3,"删除"),
    IMPORT(4,"导入"),
    ROLLBACK(6,"回滚"),
    ALL(5,"发布全部模板");

    private int code;

    private String desc;
}
