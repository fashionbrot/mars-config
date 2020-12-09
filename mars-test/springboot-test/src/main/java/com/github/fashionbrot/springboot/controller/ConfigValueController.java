package com.github.fashionbrot.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.fashionbrot.ribbon.util.CollectionUtil;
import com.github.fashionbrot.springboot.model.TestModel;
import com.github.fashionbrot.value.MarsConfigValueCache;
import com.github.fashionbrot.value.event.MarsTemplateKeyMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/value")
@Controller
public class ConfigValueController extends  MarsTemplateKeyMapping{


    @RequestMapping("get")
    @ResponseBody
    public Object test(String templateKey){

        List<TestModel> list = MarsConfigValueCache.getDeepTemplateObject(templateKey);
        if (CollectionUtil.isNotEmpty(list)){
            for(TestModel t: list){
                t.setTest(t.getTest()+"你好");
            }
        }
        return list;
    }

    @RequestMapping("get2")
    @ResponseBody
    public Object test2(String templateKey){

        List<JSONObject> list = MarsConfigValueCache.getTemplateObject(templateKey);
        if (CollectionUtil.isNotEmpty(list)) {
            JSONObject jsonObject = list.get(0);
            if (jsonObject.containsKey("title")){
                jsonObject.put("title","哈哈 ："+jsonObject.get("title"));
            }

        }
        return list;
    }

    @RequestMapping("get3")
    @ResponseBody
    public List test3(String templateKey){

        List list = MarsConfigValueCache.getTemplateObject(templateKey);
        if (CollectionUtil.isNotEmpty(list)) {

        }
        return list;
    }



    public Map<String,Class> initTemplateKeyClass() {
        Map<String,Class> map=new HashMap<>();
        map.put("test", TestModel.class);
        return map;
    }


}
