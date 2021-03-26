package com.ylf.miaosha.service;

import com.ylf.miaosha.redis.BasePrefix;

public class MiaoshaKey extends BasePrefix {

	private MiaoshaKey(String prefix) {
		super(prefix);
	}
	public static MiaoshaKey isGoodsOver = new MiaoshaKey("go");
}
