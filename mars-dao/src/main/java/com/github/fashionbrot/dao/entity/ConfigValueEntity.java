package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 配置数据表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-13
 */
@ApiModel(value = "配置数据表")
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("config_value")
public class ConfigValueEntity extends BaseEntity {


	@ApiModelProperty(value = "状态 1开启 0关闭")
	@TableField("status")
	private Integer status;

	@ApiModelProperty(value = "开始时间")
	@TableField("start_time")
	private Date startTime;

	@ApiModelProperty(value = "结束时间")
	@TableField("end_time")
	private Date endTime;

	@ApiModelProperty(value = "模板key")
	@TableField("template_key")
	private String templateKey;

	@ApiModelProperty(value = "实例json")
	@TableField("json")
	private String json;

	@ApiModelProperty(value = "描述")
	@TableField("description")
	private String description;

	@ApiModelProperty(value = "优先级")
	@TableField("priority")
	private Integer priority;

	@ApiModelProperty(value = "环境code")
	@TableField("env_code")
	private String envCode;

	@ApiModelProperty(value = "应用名")
	@TableField("app_name")
	private String appName;

	@ApiModelProperty(value = "用户名")
	@TableField("user_name")
	private String userName;

	@ApiModelProperty("发布状态 1已发布 0 未发布")
	@TableField("release_status")
	private Integer releaseStatus;
}