package com.iman.sds.po;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class LogDataReturn {
    private String factoryName;
    private List<LogDataParam> data;
}
