package com.iman.sds.po;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class LogDataParam {
    private Long sensorId;
    private String address;
    private String description;
    private Date createTime;
}
