package com.iman.sds.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author admin
 * @since 2021-07-16
 */
//每个工厂的每个水数据对应的积分操作记录（例如因为什么原因增加了积分） 包含这个记录对应在链上的hash
@Data
  @EqualsAndHashCode(callSuper = false)
    public class ScDescription implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Long id;

    private Long scoreId;

    private String description;

    @TableField("chainHash")
    private String chainHash;


}
