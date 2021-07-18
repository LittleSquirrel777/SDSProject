package com.iman.sds.po;

import com.iman.sds.entity.SensorData;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SensorDataReturn {
    private String factoryName;
    private List<SensorData> data;

}
