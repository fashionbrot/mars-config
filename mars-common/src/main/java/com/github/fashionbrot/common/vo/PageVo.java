package com.github.fashionbrot.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PageVo {


    private List<?> data;

    private long iTotalDisplayRecords;

}
