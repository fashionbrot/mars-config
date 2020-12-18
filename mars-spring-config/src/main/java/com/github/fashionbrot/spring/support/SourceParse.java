package com.github.fashionbrot.spring.support;


import com.github.fashionbrot.spring.enums.ConfigTypeEnum;

import java.io.File;
import java.util.Properties;

public interface SourceParse {

    Properties parse(String context);

    Properties fileToProperties(File file);

    ConfigTypeEnum sourceType();

}
