package com.fashion.mars.ribbon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SchemeEnum {
    HTTPS("https"),
    HTTP("http");
    private String scheme;
}
