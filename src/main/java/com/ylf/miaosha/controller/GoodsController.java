package com.ylf.miaosha.controller;

import com.ylf.miaosha.domain.MiaoshaUser;
import com.ylf.miaosha.redis.GoodsKey;
import com.ylf.miaosha.redis.RedisService;
import com.ylf.miaosha.result.Result;
import com.ylf.miaosha.service.GoodsService;
import com.ylf.miaosha.service.MiaoshaUserService;
import com.ylf.miaosha.vo.GoodsDetailVo;
import com.ylf.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoshaUserService miaoshaUserService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    RedisService redisService;
    @Autowired
    ApplicationContext applicationContext;
    @RequestMapping(value="/to_list", produces="text/html")
    @ResponseBody
    public String toList(HttpServletRequest request,HttpServletResponse response, Model model, MiaoshaUser user) {
        model.addAttribute("user", user);
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if(!StringUtils.isEmpty(html)) {
            return html;
        }
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
//    	 return "goods_list";
        SpringWebContext ctx = new SpringWebContext(request,response,
                request.getServletContext(),request.getLocale(), model.asMap(),applicationContext);
        //手动渲染
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);//设置页面级的缓存，更新缓存过期时间
        }
        return html;
    }

    @RequestMapping(value="/to_detail2/{goodsId}",produces="text/html")
    @ResponseBody
    public String detail2(HttpServletRequest request, HttpServletResponse response, Model model,MiaoshaUser user,
                          @PathVariable("goodsId")long goodsId) {
        model.addAttribute("user", user);

        //取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
        if(!StringUtils.isEmpty(html)) {
            return html;
        }
        //手动渲染
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if(now < startAt ) {//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
//        return "goods_detail";

        SpringWebContext ctx = new SpringWebContext(request,response,
                request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
        }
        return html;
    }


    @RequestMapping(value="/to_detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
                                        @PathVariable("goodsId")long goodsId) {
        //手动渲染
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if(now < startAt ) {//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo=new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setMiaoshaStatus(miaoshaStatus);
        return Result.success(vo);
    }
//    @RequestMapping("/to_detail/{goodsId}")
//    public String detail(Model model, MiaoshaUser user, @PathVariable("goodsId")long goodsId) {
//        model.addAttribute("user",user);
//        GoodsVo goods=goodsService.getGoodsVoByGoodsId(goodsId);
//        model.addAttribute("goods",goods);
//        int miaoshaStatus=0;//秒杀状态，0代表未开始，1代表活动正在进行，2代表已结束
//        int remainSeconds=0;//还有多少秒开始
//        long startAt=goods.getStartDate().getTime();
//        long endAt=goods.getEndDate().getTime();
//        long now=System.currentTimeMillis();
//        if(now<startAt)//当前时间小于秒杀开始时间，秒杀还没开始
//        {
//            miaoshaStatus=0;
//            remainSeconds=(int)(startAt-now)/1000;
//        }
//        else if(now>endAt)//秒杀已经结束
//        {
//            miaoshaStatus=2;
//            remainSeconds=-1;
//        }
//        else//秒杀正在进行
//        {
//            miaoshaStatus=1;
//            remainSeconds=0;
//        }
//        model.addAttribute("miaoshaStatus",miaoshaStatus);//秒杀状态
//        model.addAttribute("remainSeconds",remainSeconds);//秒杀剩余时间
//        return "goods_detail";
//    }

//    @RequestMapping("/to_detail")
//    public String toDetail(HttpServletResponse response,Model model,MiaoshaUser user)
//    {
//
//    }
}
