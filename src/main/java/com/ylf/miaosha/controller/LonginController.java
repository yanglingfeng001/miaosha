package com.ylf.miaosha.controller;

import com.ylf.miaosha.result.CodeMsg;
import com.ylf.miaosha.result.Result;
import com.ylf.miaosha.service.MiaoshaUserService;
import com.ylf.miaosha.util.ValidatorUtil;
import com.ylf.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LonginController {
    @Autowired
    MiaoshaUserService miaoshaUserService;
    private static Logger log= LoggerFactory.getLogger(LonginController.class);
    @RequestMapping("/to_login")
    public String toLogin()//跳转到login页面
    {
            return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLongin(HttpServletResponse response,@Valid LoginVo loginVo)
    {
        log.info(loginVo.toString());
//        //参数校验，即使前端校验以后，后端也应该进行校验
//        String passInput=loginVo.getPassword();
//        String mobile=loginVo.getMobile();
//        if(StringUtils.isEmpty(passInput))
//        {
//            return Result.error(Codemsg.PASSWORD_EMPTY);
//        }
//        if(StringUtils.isEmpty(mobile))
//        {
//            return Result.error(Codemsg.MOBILE_EMPTY);
//        }
//        if(!ValidatorUtil.isMobile(mobile))
//        {
//            return Result.error(Codemsg.MOBILE_ERROR);
//        }
        //登陆
        miaoshaUserService.login(response,loginVo);
        return Result.success(true);

    }
}
