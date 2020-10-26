package com.github.fashionbrot.console.controller.config;


import com.github.fashionbrot.common.annotation.IsMenu;
import com.github.fashionbrot.common.req.ConfigRecordReq;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.service.ConfigRecordService;
import com.github.fashionbrot.dao.entity.ConfigRecordEntity;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 配置数据记录表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-22
 */

@Controller
@RequestMapping("/admin/config/record")
@Api(tags="配置数据记录表")
@ApiSort(20670308)
public class ConfigRecordController {

    @Autowired
    private ConfigRecordService configRecordService;

    @IsMenu
    @GetMapping("/index")
    public String index(){
        return "/record/index";
    }


    @ApiOperation("数据列表—分页")
    @GetMapping("/page")
    @ResponseBody
    public RespVo page(ConfigRecordReq req){
        return  RespVo.success(configRecordService.pageList(req));
    }


    @ApiOperation("数据列表")
    @GetMapping("/queryList")
    @ResponseBody
    public RespVo queryList(@RequestParam Map<String, Object> params){
        return  RespVo.success(configRecordService.queryList(params));
    }


    @ApiOperation("根据id查询")
    @PostMapping("/selectById")
    @ResponseBody
    public RespVo selectById(Long id){
        ConfigRecordEntity data = configRecordService.selectById(id);
        return RespVo.success(data);
    }


    @ApiOperation("新增")
    @PostMapping("/insert")
    @ResponseBody
    public RespVo add(@RequestBody ConfigRecordEntity entity){
        configRecordService.insert(entity);
        return RespVo.success();
    }


    @ApiOperation("修改")
    @PostMapping("/updateById")
    @ResponseBody
    public RespVo updateById(@RequestBody ConfigRecordEntity entity){
        configRecordService.updateById(entity);
        return RespVo.success();
    }
    @ApiOperation("根据id删除")
    @PostMapping("/deleteById")
    @ResponseBody
    public RespVo deleteById(Long id){
        configRecordService.deleteById(id);
        return RespVo.success();
    }


    @ApiOperation("批量删除")
    @PostMapping("/deleteByIds")
    @ResponseBody
    public RespVo delete(@RequestBody Long[] ids){
        configRecordService.deleteBatchIds(Arrays.asList(ids));
        return RespVo.success();
    }

    @ApiOperation("回滚配置")
    @PostMapping("/rollBack")
    @ResponseBody
    public RespVo rollBack(ConfigRecordReq req){
        configRecordService.rollBack(req);
        return RespVo.success();
    }


}