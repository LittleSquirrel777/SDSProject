package com.iman.sds.po;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserLoginParam {
    private String account;
    private String password;

}
