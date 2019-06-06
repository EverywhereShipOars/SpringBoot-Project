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




    public void setObject(String key, Object object){
        set(key, JSON.toJSONString(object));
    }

    public <T> T getObject(String key, Class<T> tClass){
        String value = get(key);
        if (value != null){
            return JSON.parseObject(value, tClass);
        }
        return null;
    }










































    public static void print(int index, Object object){
        System.out.println(String.format("%d, %s", index, object.toString()));
    }

    //测试学习Jedis
    public static void main(String[] args) {
        //jedis对象默认会连接到本地的6379端口, 因此我在这写了之后，本地就可以去查得到
        Jedis jedis = new Jedis();
        jedis.flushAll();   //flush all keys from all databases

        //因为redis是一个key-value数据库，其实用起来和HashMap就很像
        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "newHello");
        print(2, jedis.get("newHello"));
        //可以设置一个过期时间
        jedis.setex("hello2", 30, "lyl");

        //一个实例：比如要显示访问量，此时有几千个人并发的访问网站，假设只使用mysql，那每个人访问的时候，其实就是对pv做了一个update的操作
        //这是非常慢的，因为update会加锁，因此只使用mysql是不现实的。
        //因此这里需要做的就是先将访问数值读入内存，然后每隔一定时间(1s)和数据库同步一次。redis也专门对一些数值型的操作做了优化
        jedis.set("pv", "100");
        jedis.incr("pv");
        print(3, jedis.get("pv"));
        jedis.incrBy("pv", 6);
        print(4, jedis.get("pv"));

        //列表操作, 一会可以搜搜这底层是个什么，好像就是个列表，因此列表支持的操作，其实它都是支持的
        String listName = "listA";
        for (int i = 0; i < 10; i++) {
            jedis.lpush("listA", "a" + i);
        }
        print(4, jedis.lrange(listName, 0, jedis.llen(listName)));
        print(5, jedis.lpop(listName));
        print(6, jedis.llen(listName));
        print(7, jedis.lindex(listName, 5));

        //此时相当于redis的value部分本身就是一个哈希表了。需要注意的是，这里的value部分，也就是哈希表，它的类型只能是数值或者String
        //因此我如果希望存对象，得用到序列化的知识
        //同理，value部分也可以是集合的，就把h改成s
        String userKey = "user12";
        jedis.hset(userKey, "name", "lyl");
        jedis.hset(userKey, "age", "22");
        jedis.hset(userKey, "phone", "13378261023");
        print(8, jedis.hget(userKey, "name"));
        print(9, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "phone");
        print(10, jedis.hgetAll(userKey));
        print(11, jedis.hkeys(userKey));
        print(12, jedis.hvals(userKey));
        print(13, jedis.hexists(userKey, "email"));
        print(14, jedis.hexists(userKey, "name"));
        //如果不存在则设置
        jedis.hsetnx(userKey, "school", "pku");
        jedis.hsetnx(userKey, "name", "xyf");
        print(15, jedis.hgetAll(userKey));

        //集合
        String key1 = "key1";
        String key2 = "key2";
        for (int i = 0; i < 10; i++) {
            jedis.sadd(key1, String.valueOf(i));
            jedis.sadd(key2, String.valueOf(i * 2));
        }
        print(16, jedis.smembers(key1));
        print(17, jedis.smembers(key2));
        //求交集并集或者不同
        print(18, jedis.sinter(key1, key2));
        print(19, jedis.sunion(key1, key2));
        print(20, jedis.sdiff(key1, key2));
        print(21, jedis.sismember(key1, "5"));
        //移除
        jedis.srem(key1, "5");
        print(22, jedis.smembers(key1));
        //看有多少个值
        print(23, jedis.scard(key2));

        //对于k-v数据库redis,它的每一个value都是一个优先队列
        //这是一个很常用的东西，例如牛客网的根据成就来获取排名就是用的这个玩意
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 75, "xyf");
        jedis.zadd(rankKey, 30, "zxx");
        jedis.zadd(rankKey, 60, "hym");
        jedis.zadd(rankKey, 100, "lyl");
        //有多少个value
        print(24, jedis.zcard(rankKey));
        print(25, jedis.zscore(rankKey, "lyl"));
        //提升某一个score
        jedis.zincrby(rankKey, 24, "xyf");
        print(25, jedis.zscore(rankKey, "xyf"));
        //查看某一个score范围内的有多少个member
        print(26, jedis.zcount(rankKey, 60, 100));
        //从小到大, 从大到小
        print(27, jedis.zrange(rankKey, 0, 1));
        print(28, jedis.zrevrange(rankKey, 0, 1));
        //遍历整个rankKey对应的优先队列
        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, 60, 100)){
            System.out.println(tuple.getElement() + ": " + tuple.getScore());
        }
        //获取排名：按从小到大排序的排名，按从大到小排序的排名
        print(29, jedis.zrank(rankKey, "lyl"));
        print(30, jedis.zrevrank(rankKey, "lyl"));

        //思考一下redis在牛客的应用的一个场景，想想下面的一些功能是怎么个实现法的
        //token也是很适合存在redis中的。
        //PV、点赞(user_id放入集合中)、关注、排行榜、验证码(有有效期，和token有点类似)、缓存(在数据库上加一层缓存，大大减少数据库压力)、
        // 异步队列: 例如点赞，此时其实就是用到了redis的list结构，这个点赞操作其实就是一个事件，会被我丢入list中，然后就不管它了，会有专门的线程去处理这个事件
    }





}
