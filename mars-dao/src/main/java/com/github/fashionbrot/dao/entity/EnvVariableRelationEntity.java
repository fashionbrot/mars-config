package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 常量和环境关系表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-13
 */
@ApiModel(value = "常量和环境关系表")
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("env_variable_relation")
public class EnvVariableRelationEntity {


	@TableId(type = IdType.AUTO)
	private Long id;

	@TableField(value = "create_id",fill = FieldFill.INSERT)
	private Long createId;

	@TableField(value="create_date",fill = FieldFill.INSERT)
	private Date createDate;


	@ApiModelProperty(value = "环境code")
	@TableField("env_code")
	private String envCode;

	@ApiModelProperty(value = "常量值")
	@TableField("variable_value")
	private String variableValue;

	@ApiModelProperty(value = "常量key")
	@TableField("variable_key")
	private String variableKey;
}