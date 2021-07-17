package com.iman.sds.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SensorInfo {
    private Long id;
    private String sensorAddress;
    private Long factoryId;
    private String factoryAddress;
}
