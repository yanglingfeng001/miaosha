package com.ylf.miaosha.redis;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.ylf.miaosha.redis.RedisConfig;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisService {
    @Autowired
    JedisPool jedisPool;
    //获取对象
    public <T> T get(KeyPrefix prefix,String key,Class<T> clazz)
    {
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            //生成真正的key
            String realKey= prefix.getPrefix()+key;
            String str=jedis.get(realKey);
            T t=StringToBean(str,clazz);
            return t;
        }
        finally{
            returnTopool(jedis);
        }
    }
    //设置对象
    public <T> Boolean set(KeyPrefix prefix,String key,T value)
    {
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            String str=beanToString(value);
            if(str==null||str.length()<=0)
            {
                return false;
            }
            //生成真正的key
            String realKey= prefix.getPrefix()+key;//类名：prefix名称+key名称
            int seconds=prefix.expireSeconds();
            if(seconds<=0)
            {
                jedis.set(realKey,str);
            }
            else
            {
                jedis.setex(realKey,seconds,str);
            }
            return true;
        }
        finally{
            returnTopool(jedis);
        }
    }
    //判断对象是否存在
    public <T> Boolean exists(KeyPrefix prefix,String key)
    {
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            //生成真正的key
            String realKey= prefix.getPrefix()+key;
            return jedis.exists(realKey);
        }
        finally{
            returnTopool(jedis);
        }
    }
    //删除对象
    public Boolean delete(KeyPrefix prefix,String key)
    {
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            //生成真正的key
            String realKey= prefix.getPrefix()+key;
            return jedis.del(realKey)>0;
        }
        finally{
            returnTopool(jedis);
        }
    }
    //增加值
    public <T> Long incr(KeyPrefix prefix,String key)
    {
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            //生成真正的key
            String realKey= prefix.getPrefix()+key;
            return jedis.incr(realKey);
        }
        finally{
            returnTopool(jedis);
        }
    }
    //减少值
    public <T> Long decr(KeyPrefix prefix,String key)
    {
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            //生成真正的key
            String realKey= prefix.getPrefix()+key;
            return jedis.decr(realKey);
        }
        finally{
            returnTopool(jedis);
        }
    }
    private void returnTopool(Jedis jedis) {
        if(jedis!=null)
            jedis.close();
    }


    public static <T> String beanToString(T value) {
        if(value==null)
        {
            return null;
        }
        Class<?> clazz=value.getClass();
        if(clazz==int.class||clazz==Integer.class)
        {
            return ""+value;
        }
        else if(clazz==String.class)
        {
            return (String)value;
        }
        else if(clazz==long.class||clazz==Long.class)
        {
            return ""+value;
        }
        else
        {
            return JSON.toJSONString(value);
        }
    }

    public static  <T> T StringToBean(String str,Class<T> clazz) {
        if(str==null||str.length()<=0)
        {
            return null;
        }
        if(clazz==int.class||clazz==Integer.class)
        {
            return (T)Integer.valueOf(str);
        }
        else if(clazz==String.class)
        {
            return (T)str;
        }
        else if(clazz==long.class||clazz==Long.class)
        {
            return (T)Long.valueOf(str);
        }
        else
        {
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
        }

    }
    public boolean delete(KeyPrefix prefix) {
        if(prefix == null) {
            return false;
        }
        List<String> keys = scanKeys(prefix.getPrefix());
        if(keys==null || keys.size() <= 0) {
            return true;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(keys.toArray(new String[0]));
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> scanKeys(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            List<String> keys = new ArrayList<String>();
            String cursor = "0";
            ScanParams sp = new ScanParams();
            sp.match("*"+key+"*");
            sp.count(100);
            do{
                ScanResult<String> ret = jedis.scan(cursor, sp);
                List<String> result = ret.getResult();
                if(result!=null && result.size() > 0){
                    keys.addAll(result);
                }
                //再处理cursor
                cursor = ret.getStringCursor();
            }while(!cursor.equals("0"));
            return keys;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
