package com.lyl.news.util;

import com.lyl.news.models.EntityType;

/**
 *  为什么需要这个RedisKeyUtil? 我们想想我们是怎么维护点赞和点踩这个状态的，我们使用的是Redis的SET来存储
 *  每一条资讯都会对应一个key，如果不对key进行一个规范的管理，就会乱掉，因此我们统一使用一个RedisKeyUtil来获取规范的key
 *  这个系统目前由于只有赞资讯这个功能，但实际上如果扩展，后续还会有很多，比如赞评论，赞答案等等。
 *  则更需要一个统一对key管理的工具了
 */
public class RedisKeyUtil {

    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENT = "EVENT";

    public static String getLikeKey(int entityId, int entityType){
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDislikeKey(int entityId, int entityType){
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getEventQueueKey(){
        return BIZ_EVENT;
    }


    //for test, 打印看看key是啥
    public static void main(String[] args) {
        System.out.println(getLikeKey(12, EntityType.ENTITY_NEWS));
    }

}
