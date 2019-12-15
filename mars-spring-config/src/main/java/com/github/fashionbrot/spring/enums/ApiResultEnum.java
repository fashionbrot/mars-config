package com.github.fashionbrot.spring.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Getter
@AllArgsConstructor
public enum ApiResultEnum {

    SUCCESS_UPDATE("1","请求成功，并且需要更新"),
    SUCCESS("0","请求成功，没有要更新的数据"),
    FAILED("-1","网络请求失败,或服务器请求失败")
    ;
   private String resultCode;

   private String resultMsg;


   private static final Map<String,ApiResultEnum> MAP=new HashMap<>();

   static {
       Arrays.stream(ApiResultEnum.values()).forEach((temp)->{
           MAP.put(temp.getResultCode(),temp);
       });
   }

   public static ApiResultEnum codeOf(String resultCode){
       return MAP.get(resultCode);
   }

}
