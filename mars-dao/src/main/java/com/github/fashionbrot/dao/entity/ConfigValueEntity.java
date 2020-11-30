package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.*;

/**
 * 配置数据表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-13
 */
@ApiModel(value = "配置数据表")
@Data
@TableName("config_value")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigValueEntity  {

	@TableId(type = IdType.AUTO)
	private Long id;

	@TableField(value = "create_id",fill = FieldFill.INSERT)
	private Long createId;

	@TableField(value="create_date",fill = FieldFill.INSERT)
	private Date createDate;

	@TableField(value = "update_id",fill = FieldFill.UPDATE)
	private Long updateId;

	@TableField(value = "update_date",fill = FieldFill.UPDATE)
	private Date updateDate;


	@ApiModelProperty(value = "优先级")
	@TableField("priority")
	private Integer priority;

	@ApiModelProperty(value = "状态 1开启 0关闭")
	@TableField("status")
	private Integer status;

	@ApiModelProperty(value = "模板key")
	@TableField("template_key")
	private String templateKey;

	@ApiModelProperty(value = "描述")
	@TableField("description")
	private String description;

	@ApiModelProperty(value = "环境code")
	@TableField("env_code")
	private String envCode;

	@ApiModelProperty(value = "应用名")
	@TableField("app_name")
	private String appName;

	@ApiModelProperty(value = "用户名")
	@TableField("user_name")
	private String userName;

	@ApiModelProperty("发布状态 1已发布 0修改 2已删除 3新增")
	@TableField("release_status")
	private Integer releaseStatus;

	@ApiModelProperty("value json")
	@TableField("json")
	private String json;

	@ApiModelProperty("temp json")
	@TableField("temp_json")
	private String tempJson;

	private transient Map<String,Object> value;

}