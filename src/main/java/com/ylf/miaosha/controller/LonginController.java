package com.ylf.miaosha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LonginController {
    @RequestMapping("/to_login")
    public String toLogin()//跳转到login页面
    {

    }
}
