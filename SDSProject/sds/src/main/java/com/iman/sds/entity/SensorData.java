package com.iman.sds.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
//具体的传感器的数据，每个传感器有多条数据
@Data
@ToString
public class SensorData implements Serializable {

    private Long id;

    private Long sensorId;

    private Integer ph;

    private Integer chroma;

    private Integer ss;

    private Integer bod5;

    private Integer cod;

    private Integer an;

    private Integer tn;

    private Integer tp;

    private Integer vp;

    private Integer toc;

    private int status;
}
