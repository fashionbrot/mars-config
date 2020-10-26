package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * 配置数据记录表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-22
 */
@ApiModel(value = "配置数据记录表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("config_record")
public class ConfigRecordEntity {
	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	@TableField(value = "create_id",fill = FieldFill.INSERT)
	private Long createId;

	@TableField(value="create_date",fill = FieldFill.INSERT)
	private Date createDate;

	@ApiModelProperty(value = "环境code")
	@TableField("env_code")
	private String envCode;

	@ApiModelProperty(value = "应用名")
	@TableField("app_name")
	private String appName;

	@ApiModelProperty(value = "模板key")
	@TableField("template_key")
	private String templateKey;

	@ApiModelProperty("配置id")
	@TableField("config_id")
	private Long configId;

	@ApiModelProperty(value = "实例json")
	@TableField("json")
	private String json;

	@ApiModelProperty(value = "实例json")
	@TableField("new_json")
	private String newJson;

	@ApiModelProperty(value = "用户名")
	@TableField("user_name")
	private String userName;

	@ApiModelProperty("操作类型 2编辑 3:删除")
	@TableField("operation_type")
	private Integer operationType;

	@ApiModelProperty(value = "描述")
	@TableField("description")
	private String description;

	@TableLogic(value = "0", delval = "1")
	@TableField(value = "del_flag",fill = FieldFill.INSERT)
	private int delFlag;
}