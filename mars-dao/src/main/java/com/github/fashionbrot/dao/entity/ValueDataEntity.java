package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导入导出记录表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-13
 */
@ApiModel(value = "导入导出记录表")
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("value_data")
public class ValueDataEntity extends BaseEntity {


	@ApiModelProperty(value = "0 导入  1导出")
	@TableField("data_type")
	private Integer dataType;

	@ApiModelProperty(value = "导出导入json数据")
	@TableField("json")
	private String json;

	@ApiModelProperty(value = "环境code")
	@TableField("env_code")
	private String envCode;

	@ApiModelProperty(value = "应用名")
	@TableField("app_name")
	private String appName;

	@ApiModelProperty(value = "用户名")
	@TableField("user_name")
	private String userName;

	@ApiModelProperty(value = "模板key")
	@TableField("template_key")
	private String templateKey;
}