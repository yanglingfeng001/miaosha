package com.ylf.miaosha.controller;

import com.ylf.miaosha.domain.User;
import com.ylf.miaosha.rabbitmq.MQSender;
import com.ylf.miaosha.redis.UserKey;
import com.ylf.miaosha.result.CodeMsg;
import com.ylf.miaosha.result.Result;
import com.ylf.miaosha.redis.RedisService;
import com.ylf.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//创建页面的话不能使用注解@Resbonsebody 如此便可以标识是数据还是页面
@Controller
@RequestMapping("/demo")
public class SampleController {
    @Autowired
    MQSender sender;
    @Autowired
    UserService userService;//自动注入服务
    @Autowired
    RedisService redisService;//自动注入服务
//    @RequestMapping("/mq/header")
//    @ResponseBody
//    public Result<String> header ()
//    {
//        sender.sendHeader("hello,coco");
//        return Result.success("hello,coco");
//    }
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public Result<String> fanout ()
//    {
//        sender.sendFanout("hello,coco");
//        return Result.success("hello,coco");
//    }
//    @RequestMapping("/mq/topic")
//    @ResponseBody
//    public Result<String> topic ()
//    {
//        sender.sendTopic("hello,coco");
//        return Result.success("hello,coco");
//    }
//
//    @RequestMapping("/mq/direct")
//    @ResponseBody
//    public Result<String> mq()
//    {
//        sender.send("hello,coco");
//        return Result.success("hello,coco");
//    }

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model)
    {
        model.addAttribute("name","coco");
        return "hello";
    }
    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello()
    {
        return Result.success("hello,imooc");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> helloerror()
    {
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet()
    {
        User user=userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx()
    {
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet()
    {
        User user=redisService.get(UserKey.getById,""+1,User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet()
    {
        User user=new User();
        user.setId(1);
        user.setName("11111");
        Boolean setResult=redisService.set(UserKey.getById,""+1,user);
        return Result.success(setResult);
    }
}
