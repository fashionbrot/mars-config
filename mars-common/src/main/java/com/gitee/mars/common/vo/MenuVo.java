package com.gitee.mars.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuVo {

    private String id;

    private String parent;

    private String text;

    private List<MenuVo> children;

}


