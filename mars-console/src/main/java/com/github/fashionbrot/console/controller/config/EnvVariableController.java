package com.github.fashionbrot.console.controller.config;


import com.github.fashionbrot.common.annotation.IsMenu;
import com.github.fashionbrot.common.req.EnvVariableReq;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.service.EnvVariableService;
import com.github.fashionbrot.dao.entity.EnvVariableEntity;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;


/**
 * 常量表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-13
 */

@Controller
@RequestMapping("/admin/variable")
@Api(tags="常量表")
@ApiSort(20510960)
public class EnvVariableController {

    @Autowired
    private EnvVariableService envVariableService;

    @IsMenu
    @GetMapping("/index")
    public String index(){
        return "/variable/index";
    }

    @ApiOperation("数据列表—分页")
    @GetMapping("/page")
    @ResponseBody
    public RespVo page(EnvVariableReq req){
        return  RespVo.success(envVariableService.pageList(req));
    }


    @ApiOperation("数据列表")
    @GetMapping("/queryList")
    @ResponseBody
    public Collection<EnvVariableEntity> queryList(@RequestParam Map<String, Object> params){
        return  envVariableService.queryList(params);
    }


    @ApiOperation("根据id查询")
    @PostMapping("/selectById")
    @ResponseBody
    public RespVo selectById( Long id){
        EnvVariableEntity data = envVariableService.selectById(id);
        return RespVo.success(data);
    }


    @ApiOperation("新增")
    @PostMapping("/insert")
    @ResponseBody
    public RespVo add(@RequestBody EnvVariableEntity entity){
        envVariableService.insert(entity);
        return RespVo.success();
    }


    @ApiOperation("修改")
    @PostMapping("/updateById")
    @ResponseBody
    public RespVo updateById(@RequestBody EnvVariableEntity entity){
        envVariableService.updateById(entity);
        return RespVo.success();
    }
    @ApiOperation("根据id删除")
    @PostMapping("/deleteById")
    @ResponseBody
    public RespVo deleteById(Long id){
        envVariableService.deleteById(id);
        return RespVo.success();
    }


    @ApiOperation("批量删除")
    @PostMapping("/deleteByIds")
    @ResponseBody
    public RespVo delete(@RequestBody Long[] ids){
        envVariableService.deleteBatchIds(Arrays.asList(ids));
        return RespVo.success();
    }


}