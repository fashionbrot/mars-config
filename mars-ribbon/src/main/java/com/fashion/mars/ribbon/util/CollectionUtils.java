package com.fashion.mars.ribbon.util;

import java.util.Collection;
import java.util.Map;

public class CollectionUtils {

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
