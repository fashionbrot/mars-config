package com.github.fashionbrot.common.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckForUpdateVo {

    /**
     * result code
     */
    private String resultCode;

    private String version;
    /**
     * for update file
     */
    private List<String> updateFiles;

    private String serverIp;
}
