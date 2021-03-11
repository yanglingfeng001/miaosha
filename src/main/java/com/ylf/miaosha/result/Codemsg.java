package com.ylf.miaosha.result;

public class Codemsg {
    private int code;
    private String msg;

    //通用异常
    public static Codemsg SUCCESS=new Codemsg(0,"SUCCESS");
    public static Codemsg SERVER_ERROR=new Codemsg(500100,"服务端异常");

    //登陆模块 5002xx
    //商品模块 5003xx
    //订单模块 5004xx
    //秒杀模块 5005xx

    private Codemsg(int code, String msg) {
        this.code=code;
        this.msg=msg;
    }

    public int getCode() {
        return code;
    }


    public String getMsg() {
        return msg;
    }

}
