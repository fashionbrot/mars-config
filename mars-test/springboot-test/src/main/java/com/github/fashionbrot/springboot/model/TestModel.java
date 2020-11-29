package com.github.fashionbrot.springboot.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class TestModel implements Serializable {

    private static final long serialVersionUID = -5651130914671708275L;


    private String test;

    private String title;

}
