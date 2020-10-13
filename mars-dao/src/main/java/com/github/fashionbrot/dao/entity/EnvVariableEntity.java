package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 常量表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-13
 */
@ApiModel(value = "常量表")
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("env_variable")
public class EnvVariableEntity {

	@TableId(type = IdType.AUTO)
	private Long id;

	@TableField(value = "create_id",fill = FieldFill.INSERT)
	private Long createId;

	@TableField(value="create_date",fill = FieldFill.INSERT)
	private Date createDate;

	@ApiModelProperty(value = "变量名称")
	@TableField("variable_name")
	private String variableName;

	@ApiModelProperty(value = "变量说明")
	@TableField("variable_desc")
	private String variableDesc;

	@ApiModelProperty(value = "变量key")
	@TableField("variable_key")
	private String variableKey;

	private transient String relation;
}