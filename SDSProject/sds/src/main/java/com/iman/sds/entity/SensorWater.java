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
//工厂水的数据  每个工厂有多个传感器即有多个数据
@Data
@ToString
public class SensorWater implements Serializable {

    private Long id;

    private Long factoryId;

    private Long sensorId;


}
