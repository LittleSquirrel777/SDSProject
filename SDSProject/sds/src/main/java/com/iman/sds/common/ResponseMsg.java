package com.iman.sds.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuhan.tracedemo.common.exception.BizException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author Chris
 * @date 2021/7/9 14:32
 * @Email:gem7991@dingtalk.com
 */


@ApiModel(value="返回参数",description="返回参数对象")
public class ResponseMsg<T> implements Serializable {
    //请求流水号
    @ApiModelProperty(value="报文编号",name="msgId",required=false)
    private String msgId;
    //返回数据
    @ApiModelProperty(value="返回数据",name="data",required=false)
    private T data;
    //状态码
    @ApiModelProperty(value="状态码",name="rspCode",required=true)
    private String rspCode;
    //描述信息
    @ApiModelProperty(value="描述信息",name="rspMsg",required=false)
    private String rspMsg;

    public ResponseMsg() {
    }

    public ResponseMsg(String rspCode, String rspMsg){
        this(null, null , rspCode, rspMsg);
    }

    public ResponseMsg(String msgId, T data, String rspCode, String rspMsg) {
        this.msgId = msgId;
        this.data = data;
        this.rspCode = rspCode;
        this.rspMsg = rspMsg;
    }


    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }



    public String getRspMsg() {
        return rspMsg;
    }

    public void setRspMsg(String rspMsg) {
        this.rspMsg = rspMsg;
    }

    public String getRspCode() {
        return rspCode;
    }

    public void setRspCode(String rspCode) {
        this.rspCode = rspCode;
    }


    public static <T> ResponseMsg<T> successResponse(){
        return new ResponseMsg<T>(null,null,"0","Operation Success");
    }

    public static <T> ResponseMsg<T> successResponse(T data){
        return new ResponseMsg<T>(null,data, StatusCode.MSG_OK.getRtCode(), StatusCode.MSG_OK.getMsgCn());
    }
    public static <T> ResponseMsg<T> errorResponse(String code ,String rspMsg){


        return new ResponseMsg<T>(null,null,code,rspMsg);


    }

    public static <T> ResponseMsg<T> errorResponse(String rspMsg){


        return new ResponseMsg<T>(null,null,"1",rspMsg);


    }


    public static <T> ResponseMsg<T> errorResponse(StatusCode StatusCode){


        return new ResponseMsg<T>(null,null,StatusCode.getRtCode(),StatusCode.getMsgCn());

    }

    public static <T> ResponseMsg<T> errorResponse(StatusCode StatusCode, T data) {
        return new ResponseMsg<T>(null, data,
                StatusCode.getRtCode(),
                StatusCode.getMsgCn());
    }



    public static <T> ResponseMsg<T> buildSuccessResponse(String msg){
        return new ResponseMsg<T>(null, null, StatusCode.MSG_OK.getRtCode(), msg);
    }

    public static <T> ResponseMsg<T> buildSuccessResponse(String rspCode, String msg){
        return new ResponseMsg<T>(null, null, rspCode, msg);
    }


    public static <T> ResponseMsg<T> errorResponse(SalixError salixError){
        return new ResponseMsg<T>(null,null,salixError.getRtCode(),salixError.getMsgCn());
    }

    public static <T> ResponseMsg<T> errorResponse(SalixError salixError, T data) {
        return new ResponseMsg<T>(null, data,
                salixError.getRtCode(),
                salixError.getMsgCn());
    }


    @Override
    public String toString() {
        return "ResponseMsg{" +
                "msgId='" + msgId + '\'' +
                ", data=" + data +
                ", rspCode='" + rspCode + '\'' +
                ", rspMsg='" + rspMsg + '\'' +
                '}';
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new BizException(e.getMessage());
        }
        return json;
    }
}

