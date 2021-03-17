package com.ylf.miaosha.service;

import com.ylf.miaosha.dao.MiaoshaUserDao;
import com.ylf.miaosha.domain.MiaoshaUser;
import com.ylf.miaosha.exception.GlobalException;
import com.ylf.miaosha.redis.MiaoShaUserKey;
import com.ylf.miaosha.redis.RedisService;
import com.ylf.miaosha.result.CodeMsg;
import com.ylf.miaosha.util.MD5Util;
import com.ylf.miaosha.util.UUIDUtil;
import com.ylf.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {
    public static final String COOKIE_NAME_TOKEN="token";
    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;
    public MiaoshaUser getById(long id)
    {
        return miaoshaUserDao.getById(id);
    }

    public boolean login(HttpServletResponse response,LoginVo loginVo) {
        if(loginVo==null)
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        String mobile=loginVo.getMobile();
        String formPass=loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser user=getById(Long.parseLong(mobile));
        if(user==null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass=user.getPassword();
        String saltDB=user.getSalt();

        if(!MD5Util.formPassToDBPass(loginVo.getPassword(),saltDB).equals(dbPass))//密码错误
        {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
//        //登陆成功，生成一个随机的cookie
//        String token= UUIDUtil.uuid();
//        //把个人信息存放到一个第三方的缓存之中
//        redisService.set(MiaoShaUserKey.token,token,user);
//        Cookie cookie=new Cookie(COOKIE_NAME_TOKEN,token);
//        cookie.setMaxAge(MiaoShaUserKey.token.expireSeconds());
//        cookie.setPath("/");
//        response.addCookie(cookie);
        //添加cookie
        addCookie(response,user);
        return true;
    }

    private void addCookie(HttpServletResponse response,MiaoshaUser miaoshaUser)
    {
        //登陆成功，生成一个随机的cookie
        String token= UUIDUtil.uuid();
        //把个人信息存放到一个第三方的缓存之中
        redisService.set(MiaoShaUserKey.token,token,miaoshaUser);
        Cookie cookie=new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoShaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmpty(token))
        {
            return null;
        }
        MiaoshaUser miaoshaUser=redisService.get(MiaoShaUserKey.token,token,MiaoshaUser.class);
        //先别急，更新一下过期时间，延长一下有效期
        if(miaoshaUser!=null) {
            addCookie(response, miaoshaUser);
        }
        return miaoshaUser;
    }
}
