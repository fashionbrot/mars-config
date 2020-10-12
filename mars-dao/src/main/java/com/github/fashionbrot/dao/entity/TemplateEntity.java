package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 模板表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-12
 */
@ApiModel(value = "模板表")
@Data
@TableName("template")
public class TemplateEntity  {

	@TableId(type = IdType.AUTO)
	private Long id;

	@TableField(value = "create_id",fill = FieldFill.INSERT)
	private Long createId;

	@TableField(value="create_date",fill = FieldFill.INSERT)
	private Date createDate;

	@ApiModelProperty(value = "应用名")
	@TableField("app_name")
	private String appName;

	@ApiModelProperty(value = "模板key")
	@TableField("template_key")
	private String templateKey;

	@ApiModelProperty(value = "模板名称")
	@TableField("template_name")
	private String templateName;

	@ApiModelProperty(value = "模板描述")
	@TableField("template_desc")
	private String templateDesc;

	@ApiModelProperty(value = "模板示例图片地址")
	@TableField("template_url")
	private String templateUrl;
}