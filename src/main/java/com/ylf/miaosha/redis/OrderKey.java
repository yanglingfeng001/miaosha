package com.ylf.miaosha.redis;

public class OrderKey extends BasePrefix {

	public OrderKey(String prefix) {
		super(prefix);
	}
	//默认永不过期
	public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");//moug=miaoshaorder uid gid
}
