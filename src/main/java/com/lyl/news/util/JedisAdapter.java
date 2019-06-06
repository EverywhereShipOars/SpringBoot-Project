package com.lyl.news.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lyl.news.models.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;

/**
 *  这个类其实可以不要，这里是使用了JedisPool来管理Jedis，就有点像线程池。
 *  这里面的一堆方法也其实就是jedis本身的方法。
 *  有了这个类，之后在其他类中使用Jedis的时候，就使用这个JedisAdapter来代替
 */
@Service
public class JedisAdapter implements InitializingBean {

    private JedisPool pool = null;

    //这玩意似乎就是说，在初始化的时候会new出来一个Jedis池
    @Override
    public void afterPropertiesSet() throws Exception{
        pool = new JedisPool();
    }

    //类比线程池，这就是从Jedis池中获取一个Jedis
    private Jedis getJedis(){
        return pool.getResource();
    }

    public String get(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return getJedis().get(key);
        }catch (Exception e){
            return null;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public void set(String key, String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
        } catch (Exception e){
            return;
        } finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long sadd(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        }catch (Exception e){
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long srem(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.srem(key, value);
        }catch (Exception e){
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public boolean sismember(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        }catch (Exception e){
            return false;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    //返回set中的元素的个数
    public long scard(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long lpush(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        }catch (Exception e){
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout, String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        }catch (Exception e){
            return null;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

}
