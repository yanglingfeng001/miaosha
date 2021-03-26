package com.ylf.miaosha.controller;

import com.ylf.miaosha.domain.MiaoshaUser;
import com.ylf.miaosha.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(Model model, MiaoshaUser miaoshaUser)
    {
        System.out.println("ok");
        return Result.success(miaoshaUser);
    }
}
