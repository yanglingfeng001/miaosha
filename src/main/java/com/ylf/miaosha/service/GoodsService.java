package com.ylf.miaosha.service;

import com.ylf.miaosha.dao.GoodsDao;
import com.ylf.miaosha.domain.Goods;
import com.ylf.miaosha.domain.MiaoshaGoods;
import com.ylf.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    //获取商品列表
    public List<GoodsVo> listGoodsVo()
    {
        return goodsDao.listGoodsVo();
    }

    //通过Id获取商品
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    //减库存
    public boolean reduceStock(GoodsVo goods) {
        MiaoshaGoods g=new MiaoshaGoods();
        g.setGoodsId(goods.getId());;
        int ret=goodsDao.reduceStock(g);
        return ret>0;
    }
    //重置库存
    public void resetStock(List<GoodsVo> goodsList) {
        for(GoodsVo goods : goodsList ) {
            MiaoshaGoods g = new MiaoshaGoods();
            g.setGoodsId(goods.getId());
            g.setStockCount(goods.getStockCount());
            goodsDao.resetStock(g);
        }
    }
}
