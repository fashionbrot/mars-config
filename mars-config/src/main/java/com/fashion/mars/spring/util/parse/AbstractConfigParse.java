package com.fashion.mars.spring.util.parse;

public abstract class AbstractConfigParse implements ConfigParse {

    @Override
    public String appId() {
        return "";
    }

    @Override
    public String envCode() {
        return "";
    }
}
