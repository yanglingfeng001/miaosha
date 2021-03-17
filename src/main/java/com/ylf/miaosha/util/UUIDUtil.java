package com.ylf.miaosha.util;

import java.util.UUID;

public class UUIDUtil {
    public static String uuid()
    {
        return UUID.randomUUID().toString().replace("-","");//去掉原生UUID的横杠
    }

    public static void main(String[] args) {
        System.out.println(uuid());
    }
}
