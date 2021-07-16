package com.iman.sds.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author admin
 * @since 2021-07-16
 */
//每个用户有信息 例如用户本身的名字和地址
@Data
@ToString
public class UserInfo implements Serializable {

    private Long userId;

    private String name;

    private String address;


}
