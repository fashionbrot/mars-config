package com.github.fashionbrot.console.controller.config.api;

import com.github.fashionbrot.common.req.ConfigValueApiReq;
import com.github.fashionbrot.common.vo.ApiRespVo;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.service.ConfigValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ConfigValueApi {

    @Autowired
    private ConfigValueService configValueService;

    @PostMapping("/api/config/value/version")
    @ResponseBody
    public RespVo checkVersion(ConfigValueApiReq apiReq){
        return RespVo.success(configValueService.checkVersion(apiReq));
    }

    @PostMapping("/api/config/value/for-data")
    @ResponseBody
    public ApiRespVo getData(ConfigValueApiReq apiReq){
        return configValueService.getData(apiReq);
    }

}
