package com.github.fashionbrot.console.controller.config;



import com.github.fashionbrot.common.req.TemplatePropertyRelationReq;
import com.github.fashionbrot.common.vo.PageVo;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.service.TemplatePropertyRelationService;
import com.github.fashionbrot.dao.entity.TemplatePropertyRelationEntity;
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
 * 模板属性关系表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-12
 */

@Controller
@RequestMapping("/admin/tpr/")
@Api(tags="模板属性关系表")
@ApiSort(20496873)
public class TemplatePropertyRelationController {

    @Autowired
    private TemplatePropertyRelationService templatePropertyRelationService;


    @ApiOperation("数据列表—分页")
    @GetMapping("/page")
    @ResponseBody
    public PageVo page(TemplatePropertyRelationReq req){
        return  templatePropertyRelationService.pageList(req);
    }


    @ApiOperation("数据列表")
    @GetMapping("/queryList")
    @ResponseBody
    public RespVo queryList(@RequestParam Map<String, Object> params){
        return  RespVo.success(templatePropertyRelationService.queryList(params));
    }


    @ApiOperation("根据id查询")
    @PostMapping("/selectById")
    @ResponseBody
    public RespVo selectById(Long id){
        TemplatePropertyRelationEntity data = templatePropertyRelationService.selectById(id);
        return RespVo.success(data);
    }


    @ApiOperation("新增")
    @PostMapping("/insert")
    @ResponseBody
    public RespVo add(@RequestBody TemplatePropertyRelationEntity entity){
        templatePropertyRelationService.insert(entity);
        return RespVo.success();
    }


    @ApiOperation("修改")
    @PostMapping("/updateById")
    @ResponseBody
    public RespVo updateById(@RequestBody TemplatePropertyRelationEntity entity){
        templatePropertyRelationService.updateById(entity);
        return RespVo.success();
    }
    @ApiOperation("根据id删除")
    @PostMapping("/deleteById")
    @ResponseBody
    public RespVo deleteById(Long id){
        templatePropertyRelationService.deleteById(id);
        return RespVo.success();
    }


    @ApiOperation("批量删除")
    @PostMapping("/deleteByIds")
    @ResponseBody
    public RespVo delete(@RequestBody Long[] ids){
        templatePropertyRelationService.deleteBatchIds(Arrays.asList(ids));
        return RespVo.success();
    }


}