package com.github.fashionbrot.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  OptionEnum {

    ADD("add"),
    MODIFY("modify"),
    DROP("drop");

    private String option;
}
