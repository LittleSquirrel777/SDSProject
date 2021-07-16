package com.iman.sds.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserLoginParam {
    private String account;
    private String password;
}
