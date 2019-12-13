package com.github.fashionbrot.spring.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckForUpdateVo {

    /**
     * result code
     */
    private String resultCode;
    /**
     * file version
     */
    private String version;
    /**
     * for update file
     */
    private List<String> updateFiles;

    private String serverIp;
}
