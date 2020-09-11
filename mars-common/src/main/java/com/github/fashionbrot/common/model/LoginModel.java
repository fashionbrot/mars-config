package com.github.fashionbrot.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginModel {

    private Long userId;

    private boolean superAdmin;

    private String userName;

    private String roleName;

}
