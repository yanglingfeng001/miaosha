package com.ylf.miaosha.controller;

import com.ylf.miaosha.domain.MiaoshaUser;
import com.ylf.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoshaUserService miaoshaUserService;
    @RequestMapping("/to_list")
    public String toList(HttpServletResponse response,Model model,MiaoshaUser user) {
        model.addAttribute("user",user);
        return "goods_list";
    }

//    @RequestMapping("/to_detail")
//    public String toDetail(HttpServletResponse response,Model model,MiaoshaUser user)
//    {
//
//    }
}
