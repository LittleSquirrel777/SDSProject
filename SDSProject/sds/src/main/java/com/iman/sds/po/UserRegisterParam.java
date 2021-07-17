package com.iman.sds.po;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserRegisterParam {
    private String account;
    private String password;
    private String name;
    private String address;
}
