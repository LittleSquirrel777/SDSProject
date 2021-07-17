package com.iman.sds.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author admin
 * @since 2021-07-16
 */
//每个工厂的每个水数据对应的积分操作记录（例如因为什么原因增加了积分）
@Data
@ToString
public class ScDescription implements Serializable {

    private Long id;

    private Long scoreId;

    private String description;

    private Date createTime;
}
