package com.ylf.miaosha.redis;

public class MiaoShaUserKey extends BasePrefix{
    public static final int TOKEN_EXPIRE=3600*24*2;
    private MiaoShaUserKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }
    public static MiaoShaUserKey token=new MiaoShaUserKey(TOKEN_EXPIRE,"tk");//相当于返回了一个前缀为UserKey:id的永不超时的KeyPrefix
}
