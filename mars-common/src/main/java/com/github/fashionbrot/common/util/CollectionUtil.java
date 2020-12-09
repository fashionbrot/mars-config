package com.github.fashionbrot.common.util;

import java.util.Collection;
import java.util.Map;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
public class CollectionUtil {

    public static boolean isEmpty( Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty( Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?,?> map){
        return !isEmpty(map);
    }

    public static boolean isNotEmpty( Collection<?> collection) {
        return !isEmpty(collection);
    }
}
