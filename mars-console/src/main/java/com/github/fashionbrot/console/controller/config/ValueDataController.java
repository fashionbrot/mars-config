package com.github.fashionbrot.console.controller.config;



import com.github.fashionbrot.common.annotation.IsMenu;
import com.github.fashionbrot.common.req.ValueDataReq;
import com.github.fashionbrot.common.vo.PageVo;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.service.ValueDataService;
import com.github.fashionbrot.dao.entity.ValueDataEntity;
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
 * 导入导出记录表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-13
 */

@Controller
@RequestMapping("/admin/data")
@Api(tags="导入导出记录表")
@ApiSort(20514926)
public class ValueDataController {

    @Autowired
    private ValueDataService valueDataService;

    @IsMenu
    @GetMapping("/index")
    public String index(){
        return "/value/data";
    }


    @ApiOperation("数据列表—分页")
    @GetMapping("/page")
    @ResponseBody
    public PageVo page(ValueDataReq req){
        return  valueDataService.pageList(req);
    }


    @ApiOperation("数据列表")
    @GetMapping("/queryList")
    @ResponseBody
    public Collection<ValueDataEntity> queryList(@RequestParam Map<String, Object> params){
        return  valueDataService.queryList(params);
    }


    @ApiOperation("根据id查询")
    @PostMapping("/selectById")
    @ResponseBody
    public RespVo selectById(Long id){
        ValueDataEntity data = valueDataService.selectById(id);
        return RespVo.success(data);
    }


    @ApiOperation("新增")
    @PostMapping("/insert")
    @ResponseBody
    public RespVo add(@RequestBody ValueDataEntity entity){
        valueDataService.insert(entity);
        return RespVo.success();
    }


    @ApiOperation("修改")
    @PostMapping("/updateById")
    @ResponseBody
    public RespVo updateById(@RequestBody ValueDataEntity entity){
        valueDataService.updateById(entity);
        return RespVo.success();
    }
    @ApiOperation("根据id删除")
    @PostMapping("/deleteById")
    @ResponseBody
    public RespVo deleteById(Long id){
        valueDataService.deleteById(id);
        return RespVo.success();
    }


    @ApiOperation("批量删除")
    @PostMapping("/deleteByIds")
    @ResponseBody
    public RespVo delete(@RequestBody Long[] ids){
        valueDataService.deleteBatchIds(Arrays.asList(ids));
        return RespVo.success();
    }


}