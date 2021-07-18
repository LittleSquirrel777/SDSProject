package com.iman.sds.po;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class QueryLogParam {
    private String factoryName;
    private String address;
    private Date startTime;
    private Date endTime;
}
