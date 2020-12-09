package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 系统配置数据发布表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-11-30
 */
@ApiModel(value = "系统配置数据发布表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("system_release")
public class SystemReleaseEntity {

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

	@ApiModelProperty("文件名")
	@TableField("files")
	private String files;

	@ApiModelProperty("发布状态 1 发布 0未发布")
	@TableField("release_flag")
	private Integer releaseFlag;
}