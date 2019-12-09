package com.fashion.mars.ribbon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Getter
@AllArgsConstructor
public enum SchemeEnum {
    HTTPS("https"),
    HTTP("http");
    private String scheme;
}
