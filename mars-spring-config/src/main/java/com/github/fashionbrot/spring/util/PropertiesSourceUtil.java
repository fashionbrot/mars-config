package com.github.fashionbrot.spring.util;

import com.github.fashionbrot.spring.enums.ConfigTypeEnum;
import com.github.fashionbrot.spring.support.PropertiesSourceParse;
import com.github.fashionbrot.spring.support.SourceParseFactory;
import com.github.fashionbrot.spring.support.YamlSourceParse;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;

@Slf4j
public class PropertiesSourceUtil {

    public static Properties toProperties(final String context, ConfigTypeEnum configTypeEnum) {
        return SourceParseFactory.getSourceParse(configTypeEnum).parse(context);
    }

    public static Properties toProperties(final File file, ConfigTypeEnum configTypeEnum) {
        return SourceParseFactory.getSourceParse(configTypeEnum).fileToProperties(file);
    }

}
