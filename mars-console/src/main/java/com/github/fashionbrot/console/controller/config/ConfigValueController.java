package com.github.fashionbrot.console.controller.config;



import com.github.fashionbrot.common.annotation.IsMenu;
import com.github.fashionbrot.common.req.ConfigValueReq;
import com.github.fashionbrot.common.vo.PageVo;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.service.ConfigValueService;
import com.github.fashionbrot.dao.entity.ConfigValueEntity;
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
 * 配置数据表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-13
 */

@Controller
@RequestMapping("/admin/config/value")
@Api(tags="配置数据表")
@ApiSort(20514926)
public class ConfigValueController {

    @Autowired
    private ConfigValueService configValueService;

    @IsMenu
    @GetMapping("/index")
    public String index(){
        return "/value/index";
    }


    @ApiOperation("数据列表—分页")
    @GetMapping("/page")
    @ResponseBody
    public RespVo page(ConfigValueReq req){
        return  RespVo.success(configValueService.pageList(req));
    }


    @ApiOperation("数据列表")
    @GetMapping("/queryList")
    @ResponseBody
    public Collection<ConfigValueEntity> queryList(@RequestParam Map<String, Object> params){
        return  configValueService.queryList(params);
    }


    @ApiOperation("根据id查询")
    @PostMapping("/selectById")
    @ResponseBody
    public RespVo selectById(Long id){
        ConfigValueEntity data = configValueService.selectById(id);
        return RespVo.success(data);
    }


    @ApiOperation("新增")
    @PostMapping("/insert")
    @ResponseBody
    public RespVo add(@RequestBody ConfigValueEntity entity){
        configValueService.insert(entity);
        return RespVo.success();
    }


    @ApiOperation("修改")
    @PostMapping("/updateById")
    @ResponseBody
    public RespVo updateById(@RequestBody ConfigValueEntity entity){
        configValueService.updateById(entity);
        return RespVo.success();
    }

    @ApiOperation("根据id删除")
    @PostMapping("/deleteById")
    @ResponseBody
    public RespVo deleteById(Long id){
        configValueService.deleteById(id);
        return RespVo.success();
    }

    @ApiOperation("撤销删除")
    @PostMapping("/unDeleteById")
    @ResponseBody
    public RespVo unDeleteById(Long id){
        configValueService.unDeleteById(id);
        return RespVo.success();
    }


    @ApiOperation("批量删除")
    @PostMapping("/deleteByIds")
    @ResponseBody
    public RespVo delete(@RequestBody Long[] ids){
        configValueService.deleteBatchIds(Arrays.asList(ids));
        return RespVo.success();
    }

    @ApiOperation("发布")
    @PostMapping("/release")
    @ResponseBody
    public RespVo release(ConfigValueReq req){
        configValueService.release(req);
        return RespVo.success();
    }

}