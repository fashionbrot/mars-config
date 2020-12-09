package com.github.fashionbrot.console.controller.config;



import com.github.fashionbrot.common.annotation.IsMenu;
import com.github.fashionbrot.common.req.PropertyReq;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.service.PropertyService;
import com.github.fashionbrot.dao.entity.PropertyEntity;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * 属性表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-12
 */

@Controller
@RequestMapping("/admin/property")
@Api(tags="属性表")
@ApiSort(20496873)
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @IsMenu
    @GetMapping("/index")
    public String index(){
        return "/template/property";
    }

    @ApiOperation("数据列表—分页")
    @GetMapping("/page")
    @ResponseBody
    public RespVo page(PropertyReq req){
        return  RespVo.success(propertyService.pageList(req));
    }


    @ApiOperation("数据列表")
    @GetMapping("/queryList")
    @ResponseBody
    public Collection<PropertyEntity> queryList(@RequestParam Map<String, Object> params){
        return  propertyService.queryList(params);
    }

    @ApiOperation("数据列表")
    @GetMapping("/queryListVo")
    @ResponseBody
    public RespVo queryListVo(String appName,String templateKey){
        Map<String,Object> map=new HashMap<>();
        if (StringUtils.isNotEmpty(appName) ){
            map.put("app_name",appName);
        }
        if (StringUtils.isNotEmpty(templateKey) ){
            map.put("template_key",templateKey);
        }
        return  RespVo.success(propertyService.queryList(map));
    }

    @ApiOperation("复制属性")
    @PostMapping("/copyProperty")
    @ResponseBody
    public RespVo copyProperty(Long[] ids,String appName,String templateKey){
        propertyService.copyProperty(ids,appName,templateKey);
        return RespVo.success();
    }

    @ApiOperation("生成java bean")
    @PostMapping("/code")
    @ResponseBody
    public RespVo codeProperty(String appName,String templateKey){
        return RespVo.success(propertyService.codeProperty(appName,templateKey));
    }

    @ApiOperation("根据id查询")
    @PostMapping("/selectById")
    @ResponseBody
    public RespVo selectById(Long id){
        PropertyEntity data = propertyService.selectById(id);
        return RespVo.success(data);
    }


    @ApiOperation("新增")
    @PostMapping("/insert")
    @ResponseBody
    public RespVo add(@RequestBody PropertyEntity entity){
        propertyService.insert(entity);
        return RespVo.success();
    }


    @ApiOperation("修改")
    @PostMapping("/updateById")
    @ResponseBody
    public RespVo updateById(@RequestBody PropertyEntity entity){
        propertyService.updateById(entity);
        return RespVo.success();
    }


    @ApiOperation("根据id删除")
    @PostMapping("/deleteById")
    @ResponseBody
    public RespVo deleteById(Long id){
        propertyService.deleteById(id);
        return RespVo.success();
    }


    @ApiOperation("批量删除")
    @PostMapping("/deleteByIds")
    @ResponseBody
    public RespVo delete(Long[] ids){
        propertyService.deleteBatchIds(Arrays.asList(ids));
        return RespVo.success();
    }


}