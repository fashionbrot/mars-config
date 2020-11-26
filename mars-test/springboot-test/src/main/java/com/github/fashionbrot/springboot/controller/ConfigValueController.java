package com.github.fashionbrot.springboot.controller;

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

        List<TestModel> list = MarsConfigValueCache.getTemplateObject(templateKey);
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

        List<TestModel> list = MarsConfigValueCache.getTemplateObject(templateKey);

        return list;
    }


    public Map<String,Class> initTemplateKeyClass() {
        Map<String,Class> map=new HashMap<>();
        map.put("test", TestModel.class);
        return map;
    }


}
