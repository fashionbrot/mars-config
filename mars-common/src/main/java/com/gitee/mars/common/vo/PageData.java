package com.gitee.mars.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageData<T> {

    private List<T> data;

    private long iTotalDisplayRecords;
    private long iTotalRecords;

}
