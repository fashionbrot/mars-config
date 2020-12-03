package com.github.fashionbrot.spring.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForDataVoList {

    private List<ForDataVo> list;

    private Long version;
}
