package com.ylf.miaosha.redis;

public class UserKey extends BasePrefix{
    private UserKey( String prefix) {
        super(prefix);
    }
    public static UserKey getById=new UserKey("id");//相当于返回了一个前缀为UserKey:id的永不超时的KeyPrefix
    public static UserKey getByName=new UserKey("name");
}
