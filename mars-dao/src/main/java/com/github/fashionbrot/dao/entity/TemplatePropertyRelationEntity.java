package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 模板属性关系表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-12
 */
@ApiModel(value = "模板属性关系表")
@Data
@TableName("template_property_relation")
public class TemplatePropertyRelationEntity  {

	@TableId(type = IdType.AUTO)
	private Long id;

	@TableField(value = "create_id",fill = FieldFill.INSERT)
	private Long createId;

	@TableField(value="create_date",fill = FieldFill.INSERT)
	private Date createDate;

	@ApiModelProperty(value = "模板key")
	@TableField("template_key")
	private String templateKey;

	@ApiModelProperty(value = "属性key")
	@TableField("property_key")
	private String propertyKey;

	@ApiModelProperty(value = "应用名称")
	@TableField("app_name")
	private String appName;

	@ApiModelProperty(value = "显示优先级")
	@TableField("priority")
	private Integer priority;
}