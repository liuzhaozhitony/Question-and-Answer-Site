package com.nowcoder.service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * Created by LIU ZHAOZHI on 2017-7-18.
 */
@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    //用户可关注了某个实体，可以是问题，其他用户，或者评论
    public boolean follow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        Date date = new Date();
        //实体的粉丝增加当前用户
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.zadd(followerKey,date.getTime(),String.valueOf(userId));
        //当前用户对这类实体关注+1
        tx.zadd(followeeKey,date.getTime(),String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx,jedis);

        return ret.size()==2 && (Long)ret.get(0) >0 && (Long)ret.get(1)>0;
    }

    public boolean unfollow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.zrem(followerKey,String.valueOf(userId));
        tx.zrem(followeeKey,String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx,jedis);

        return ret.size()==2 && (Long)ret.get(0) >0 && (Long)ret.get(1)>0;
    }

    public List<Integer> getIdsFromSet(Set<String> idset){
        List<Integer> ids = new ArrayList<>();
        for(String str:idset){
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }

    public List<Integer> getFollowers(int entityType, int entityId, int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey,0,count));
    }

    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey,offset,count));
    }

    public List<Integer> getFollowees(int entityType, int entityId, int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,0,count));
    }

    public List<Integer> getFollowees(int entityType, int entityId, int offset, int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,offset,count));
    }

    public long getFollowerCount(int entityId, int entityType){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return jedisAdapter.zcard(followerKey);
    }

    public long getFolloweeCount(int entityId, int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType,entityId);
        return jedisAdapter.zcard(followeeKey);
    }

    //判断是否关注了某个实体
    public boolean isFollower(int userId, int entityId, int entityType){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return jedisAdapter.zscore(followerKey,String.valueOf(userId)) != null;
    }


}
