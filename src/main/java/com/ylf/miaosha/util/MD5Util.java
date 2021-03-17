package com.ylf.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    //通过两次md5，防止黑客通过彩虹表反查询密码
    public static String md5(String str)
    {
        return DigestUtils.md5Hex(str);//明文转化为md5
    }
    private static final String salt="1a2b3c4d";
    public static String intputPassToFormPass(String intputPass) {
        String res = "" + salt.charAt(0) + salt.charAt(2) + intputPass + salt.charAt(5) + salt.charAt(4);
        return md5(res);
    }
    public static String formPassToDBPass(String formPass,String saltDB) {
        String res = "" + saltDB.charAt(0) + saltDB.charAt(2) + formPass + saltDB.charAt(5) + saltDB.charAt(4);
        return md5(res);
    }
    public static String inputPassToDBPass(String input,String saltDB)
    {
        String formPass=intputPassToFormPass(input);
        String dbPass=formPassToDBPass(formPass,saltDB);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToDBPass("123456","1a2b3c4d"));
        System.out.println(intputPassToFormPass("123456"));
    }
}
