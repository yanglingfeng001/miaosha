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

    //从缓存里或者数据库中取得user信息
    public MiaoshaUser getById(long id)
    {
         //从缓存里取
        MiaoshaUser user=redisService.get(MiaoShaUserKey.getById,""+id,MiaoshaUser.class);
        if(user!=null)//缓存里没有
        {
            return user;
        }
        user=miaoshaUserDao.getById(id);//从数据库取
        if(user!=null)//数据库里取到
        {
            redisService.set(MiaoShaUserKey.getById,""+id,user);//放到缓存里面去
        }
        return user;
    }
    public boolean updatePassword(String token,long id,String passwordNew)
    {
        //取user
        MiaoshaUser user=getById(id);
        if(user==null)
        {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        MiaoshaUser toBeUpdate=new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(passwordNew,user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        //处理缓存
        redisService.delete(MiaoShaUserKey.getById,""+id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoShaUserKey.token,token,user);
        return true;
    }

    //首先通过参数校验，然后比对用户密码，如果登陆成功，添加cookie或者更新老cookie的时间
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
        //登陆成功，生成一个随机的cookie
        String token= UUIDUtil.uuid();
        addCookie(response,token,user);
        return true;
    }

    //添加cookie到本地，然后在服务器redis中缓存用户信息
    private void addCookie(HttpServletResponse response,String token,MiaoshaUser miaoshaUser)
    {
        //没必要每次都新建一个token，用原来的老token就行了
        //把个人信息存放到一个第三方的缓存之中，并把生成的token放在浏览器缓存中
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
            addCookie(response, token,miaoshaUser);
        }
        return miaoshaUser;
    }
}
