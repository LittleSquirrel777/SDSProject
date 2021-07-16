package com.iman.sds.controller;

import com.iman.sds.common.log.SalixLog;

/**
 * @author Chris
 * @date 2021/7/12 21:50
 * @Email:gem7991@dingtalk.com
 */
public class BaseController {
    private static ThreadLocal<SalixLog> logThreadLocal = new ThreadLocal<>();

    public SalixLog getSalixLog() {
        return logThreadLocal.get();
    }

    public SalixLog initSalixLog() {
        logThreadLocal.set(new SalixLog());
        return logThreadLocal.get();
    }
}
