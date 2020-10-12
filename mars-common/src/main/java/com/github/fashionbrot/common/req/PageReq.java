package com.github.fashionbrot.common.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "分页req")
public class PageReq {

    @ApiModelProperty(value = "当前页码")
    private int start;
    @ApiModelProperty(value = "每页条数")
    private int length;

    @ApiModelProperty(value = "当前页码")
    private int page;
    @ApiModelProperty(value = "每页条数")
    private int pageSize;
}
