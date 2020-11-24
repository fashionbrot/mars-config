package com.github.fashionbrot.value.model;


import lombok.Data;

import java.util.List;


@Data
public class Resp{


    private int code;
    private String msg;
    private List<ConfigValue> data;
    private Long version;


    public boolean isSuccess(){
        return this.code==0;
    }

}
