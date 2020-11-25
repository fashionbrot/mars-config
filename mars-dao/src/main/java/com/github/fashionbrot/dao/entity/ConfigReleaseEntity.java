package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * 配置数据发布表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-11-23
 */
@ApiModel(value = "配置数据发布表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("config_release")
public class ConfigReleaseEntity{

	@TableId(type = IdType.AUTO)
	private Long id;

	@TableField(value = "update_date",fill = FieldFill.UPDATE)
	private Date updateDate;

	@ApiModelProperty(value = "环境code")
	@TableField("env_code")
	private String envCode;

	@ApiModelProperty(value = "应用名")
	@TableField("app_name")
	private String appName;

	/*@ApiModelProperty(value = "版本")
	@TableField("version")
	private Long version;*/

	@ApiModelProperty("模板keys")
	@TableField("template_keys")
	private String templateKeys;

	@ApiModelProperty("发布状态 1 发布 0未发布")
	@TableField("release_flag")
	private Integer releaseFlag;
}