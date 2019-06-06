package com.lyl.news.service;

import com.lyl.news.models.HostHolder;
import com.lyl.news.util.JedisAdapter;
import com.lyl.news.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *  对于点赞和点踩的Service，主要是三个功能逻辑：点赞、点踩、显示当前是否点赞或者点踩的状态
 */
@Service
public class LikeService {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private JedisAdapter jedisAdapter;

    //判断用户的点赞和点踩状态
    public int getLikeStatus(int userId , int entityId, int entityType){
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityId, entityType);
        //赞过，即这个userId在这条新闻资讯的所维护的赞的集合中，返回1
        if (jedisAdapter.sismember(likeKey, String.valueOf(userId))){
            return 1;
        }
        //踩过，返回-1; 没赞没踩返回0
        return jedisAdapter.sismember(dislikeKey, String.valueOf(userId)) ? -1 : 0;
    }

    //点赞操作，其实就是把当前登陆用户的id放到这个资讯对应的集合中
    //这里的逻辑是赞和踩不能共存，即如果它本身在踩的集合中，就要先移除然后再放入
    //返回值是当前资讯所维护的点赞集合中的userId个数
    public long like(int userId,  int entityId, int entityType){
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityId, entityType);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));
        jedisAdapter.srem(dislikeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }

    //点踩操作和点赞一个逻辑
    public long dislike(int userId, int entityId, int entityType){
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityId, entityType);
        jedisAdapter.sadd(dislikeKey, String.valueOf(userId));
        jedisAdapter.srem(likeKey, String.valueOf(userId));
        return jedisAdapter.scard(dislikeKey);
    }

}
