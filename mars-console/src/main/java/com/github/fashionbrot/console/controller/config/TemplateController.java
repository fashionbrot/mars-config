package com.github.fashionbrot.console.controller.config;

import com.github.fashionbrot.common.annotation.IsMenu;
import com.github.fashionbrot.common.req.TemplateReq;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.service.TemplateService;
import com.github.fashionbrot.dao.entity.TemplateEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin/template")
public class TemplateController {

    @Autowired
    private TemplateService templateService;


    @IsMenu
    @GetMapping("/index")
    public String index(){
        return "/template/index";
    }


    @ApiOperation("数据列表—分页")
    @GetMapping("/page")
    @ResponseBody
    public RespVo page(TemplateReq req){
        return  RespVo.success(templateService.pageList(req));
    }

    @ApiOperation("数据列表")
    @GetMapping("/list")
    @ResponseBody
    public RespVo list(TemplateReq req){
        return  RespVo.success(templateService.list(req));
    }


    @RequestMapping("add")
    @ResponseBody
    public RespVo add(@RequestBody TemplateEntity templateInfo) {
        templateService.insert(templateInfo);
        return RespVo.success();
    }


    @RequestMapping("/update")
    @ResponseBody
    public RespVo update(@RequestBody TemplateEntity templateInfo) {
        templateService.updateById(templateInfo);
        return RespVo.success();
    }

    @ApiOperation("根据id删除")
    @PostMapping("/deleteById")
    @ResponseBody
    public RespVo deleteById(Long id){
        templateService.deleteById(id);
        return RespVo.success();
    }

    @ApiOperation("根据id查询")
    @PostMapping("/selectById")
    @ResponseBody
    public RespVo selectById( Long id){
        TemplateEntity data = templateService.selectById(id);
        return RespVo.success(data);
    }



}
