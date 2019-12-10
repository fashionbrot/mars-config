package com.gitee.mars.console.controller;

import com.gitee.mars.common.req.DataConfigReq;
import com.gitee.mars.common.vo.CheckForUpdateVo;
import com.gitee.mars.common.vo.ForDataVo;
import com.gitee.mars.common.vo.RespVo;
import com.gitee.mars.core.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */

@RequestMapping("/api")
@Controller
public class ApiConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @RequestMapping("/health")
    public RespVo health(){
        return RespVo.success();
    }



    @PostMapping("/config/check-for-update")
    @ResponseBody
    public CheckForUpdateVo checkForUpdate(DataConfigReq dataConfig){
        return systemConfigService.checkForUpdate(dataConfig);
    }

    @PostMapping("/config/for-data")
    @ResponseBody
    public ForDataVo forDataVo(DataConfigReq dataConfig){
        return systemConfigService.forDataVo(dataConfig);
    }

}
