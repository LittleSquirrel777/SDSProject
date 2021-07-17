package com.iman.sds.entity;

import lombok.Data;
import lombok.ToString;

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
public class FactorySensor {
    private Long id;
    private Long factoryId;
    private Long sensorId;
}
