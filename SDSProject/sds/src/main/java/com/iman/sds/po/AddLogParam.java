package com.iman.sds.po;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class AddLogParam {
    private Long id;
    private String factoryName;
    private Long sensorId;
    private int num;
    private String description;
    private Date createTime;
}
