package com.iman.sds.po;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class AddDataParam {
    private Long id;
    private Long sensorId;
    private Date creteTime;
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
}
