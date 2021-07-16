package com.iman.sds.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@Data
@ToString
public class User {
    private Long id;
    private String name;
    private String password;
    private String salt;

    private boolean isAuthenticated;
    private int status;
}

