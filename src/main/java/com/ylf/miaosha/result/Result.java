package com.ylf.miaosha.result;

public class Result <T>{
    private int code;
    private String msg;
    private T data;

    private Result(T data) {//是正确的时候只需要传入string类型的表示正确的信息
        this.code=0;
        this.msg ="success";
        this.data=data;
    }
    private Result(CodeMsg cm) {//是错误的时候需要传入自定义类型的Codemsg信息
        if(cm==null)
            return;
        this.code=cm.getCode();
        this.msg=cm.getMsg();
    }
    public static <T> Result<T> success(T data)
    {
        return new Result<T>(data);
    }
    public static <T> Result<T> error(CodeMsg cm)
    {
        return new Result<T>(cm);
    }
    public int getCode() {
        return code;
    }


    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

}
