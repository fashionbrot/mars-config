package com.github.fashionbrot.console.controller.config;



import com.github.fashionbrot.common.req.EnvVariableRelationReq;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.service.EnvVariableRelationService;
import com.github.fashionbrot.dao.entity.EnvVariableRelationEntity;
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
 * 常量和环境关系表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-13
 */

@Controller
@RequestMapping("/admin/variable-relation")
@Api(tags="常量和环境关系表")
@ApiSort(20510960)
public class EnvVariableRelationController {

    @Autowired
    private EnvVariableRelationService envVariableRelationService;


    @ApiOperation("数据列表—分页")
    @GetMapping("/page")
    @ResponseBody
    public RespVo page(EnvVariableRelationReq req){
        return  RespVo.success(envVariableRelationService.pageList(req));
    }


    @ApiOperation("数据列表")
    @GetMapping("/queryList")
    @ResponseBody
    public RespVo queryList(@RequestParam Map<String, Object> params){
        return  RespVo.success(envVariableRelationService.queryList(params));
    }


    @ApiOperation("根据id查询")
    @PostMapping("/selectById")
    @ResponseBody
    public RespVo selectById( Long id){
        EnvVariableRelationEntity data = envVariableRelationService.selectById(id);
        return RespVo.success(data);
    }


    @ApiOperation("新增")
    @PostMapping("/insert")
    @ResponseBody
    public RespVo add(@RequestBody EnvVariableRelationEntity entity){
        envVariableRelationService.insert(entity);
        return RespVo.success();
    }


    @ApiOperation("修改")
    @PostMapping("/updateById")
    @ResponseBody
    public RespVo updateById(@RequestBody EnvVariableRelationEntity entity){
        envVariableRelationService.updateById(entity);
        return RespVo.success();
    }
    @ApiOperation("根据id删除")
    @PostMapping("/deleteById")
    @ResponseBody
    public RespVo deleteById(Long id){
        envVariableRelationService.deleteById(id);
        return RespVo.success();
    }


    @ApiOperation("批量删除")
    @PostMapping("/deleteByIds")
    @ResponseBody
    public RespVo delete(@RequestBody Long[] ids){
        envVariableRelationService.deleteBatchIds(Arrays.asList(ids));
        return RespVo.success();
    }


}