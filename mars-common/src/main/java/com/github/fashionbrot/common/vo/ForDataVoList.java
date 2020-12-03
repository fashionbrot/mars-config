package com.github.fashionbrot.common.vo;

import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForDataVoList {

    private List<ForDataVo> list;

    private Long version;
}
