package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * 属性表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-12
 */
@ApiModel(value = "属性表")
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("property")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyEntity {

	@TableId(type = IdType.AUTO)
	private Long id;

	@TableField(value = "create_id",fill = FieldFill.INSERT)
	private Long createId;

	@TableField(value="create_date",fill = FieldFill.INSERT)
	private Date createDate;

	@ApiModelProperty(value = "属性名称")
	@TableField("property_name")
	private String propertyName;

	@ApiModelProperty(value = "属性key")
	@TableField("property_key")
	private String propertyKey;

	@ApiModelProperty(value = "属性类型")
	@TableField("property_type")
	private String propertyType;

	@ApiModelProperty(value = "html标签类型")
	@TableField("label_type")
	private String labelType;

	@ApiModelProperty(value = "html 标签值")
	@TableField("label_value")
	private String labelValue;

	@ApiModelProperty(value = "应用名称")
	@TableField("app_name")
	private String appName;

	@ApiModelProperty(value = "常量key")
	@TableField("variable_key")
	private String variableKey;

	@ApiModelProperty(value = "模板key ，公共属性为空，指定模板属性不为空")
	@TableField("template_key")
	private String templateKey;

	@ApiModelProperty(value = "0 公共属性 1 模板属性")
	@TableField("attribute_type")
	private Integer attributeType;

	@ApiModelProperty(value = "显示优先级")
	@TableField("priority")
	private Integer priority;

	@TableLogic(value = "0", delval = "1")
	@TableField(value = "del_flag",fill = FieldFill.INSERT)
	private int delFlag;
}