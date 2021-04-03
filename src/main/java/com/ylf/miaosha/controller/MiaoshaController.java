package com.ylf.miaosha.controller;

import com.ylf.miaosha.access.AccessLimit;
import com.ylf.miaosha.domain.MiaoshaOrder;
import com.ylf.miaosha.domain.MiaoshaUser;
import com.ylf.miaosha.domain.OrderInfo;
import com.ylf.miaosha.rabbitmq.MQSender;
import com.ylf.miaosha.rabbitmq.MiaoshaMessage;
import com.ylf.miaosha.redis.*;
import com.ylf.miaosha.result.CodeMsg;
import com.ylf.miaosha.result.Result;
import com.ylf.miaosha.service.GoodsService;
import com.ylf.miaosha.service.MiaoshaService;
import com.ylf.miaosha.service.OrderService;
import com.ylf.miaosha.util.MD5Util;
import com.ylf.miaosha.util.UUIDUtil;
import com.ylf.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;
    @Autowired
    RedisService redisService;
    @Autowired
    MQSender sender;

    private Map<Long,Boolean> localOverMap=new HashMap<>();


    //在商品开始秒杀之后才能获取秒杀地址，通过验证码验证后返回一个真正的秒杀地址，然后通过将秒杀地址和需要的数据重新传入判断是否秒杀成功
    @AccessLimit(seconds=5,maxCount=5,needLogin=true)
    @RequestMapping(value="/path", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request,
                                         MiaoshaUser user,
                                         @RequestParam("goodsId")Long goodsId,
                                         @RequestParam(value = "verifyCode",defaultValue = "0")int verifyCode) {
        //判断用户是否登陆
        if(user==null)
        {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean check=miaoshaService.checkVerifyCode(user,goodsId,verifyCode);
        if(!check)
        {
            return Result.error(CodeMsg.VERIFYCODE);//非法请求
        }
        String path  =miaoshaService.createMiaoshaPath(user, goodsId);
        return Result.success(path);
    }

    @RequestMapping(value="/reset", method=RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for(GoodsVo goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), 10);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
        redisService.delete(MiaoshaKey.isGoodsOver);
        miaoshaService.reset(goodsList);
        return Result.success(true);
    }

    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     * */
    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model,MiaoshaUser user,
                                      @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result  =miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }
/*
* GET与POST有什么区别？
* GET幂等：无论多少次产生的结果一样
* POST非幂等：对服务端的数据会产生影响的
* */
    @RequestMapping(value="/{path}/do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser user,
                                   @RequestParam("goodsId")Long goodsId,
                                   @PathVariable("path")String path) {
        model.addAttribute("user",user);
        //判断用户是否登陆
        if(user==null)
        {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证path
        boolean check=miaoshaService.checkPath(user,goodsId,path);
        if(!check)
        {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存标记减少redis访问
        boolean over=localOverMap.get(goodsId);
        if(over)
        {
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //减库存
        long stock=redisService.decr(GoodsKey.getMiaoshaGoodsStock,""+goodsId);//当多余的请求就不用去请求redis服务器了
        if(stock<0)
        {
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order=orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order!=null)
        {
            return Result.error(CodeMsg.REPEAT_MIAOSHA);
        }
        //入队
        MiaoshaMessage mm=new MiaoshaMessage();
        mm.setGoodsId(goodsId);
        mm.setUser(user);
        sender.sendMiaoshaMessage(mm);
        return  Result.success(0);//排队中

/*        //判断库存
        GoodsVo goods=goodsService.getGoodsVoByGoodsId(goodsId);
        if(goods.getStockCount()<=0) {
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order=orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order!=null)
        {
            return Result.error(CodeMsg.REPEAT_MIAOSHA);
        }
        //真正的秒杀
        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo=miaoshaService.miaosha(user,goods);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goods);
        return Result.success(orderInfo);*/
    }
    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCode(HttpServletResponse response,MiaoshaUser user,
                                              @RequestParam("goodsId")long goodsId) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image  = miaoshaService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }

    //初始化将数据库中的库存读取到redis中来
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList == null) {
            return;
        }
        for(GoodsVo goods : goodsList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(),false);
        }
    }


}
