package com.iman.sds.po;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class QueryDataParam {
    private String factoryName;
    private String address;
    private String startTime;
    private String endTime;
}
