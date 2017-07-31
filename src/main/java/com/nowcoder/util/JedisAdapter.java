package com.nowcoder.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by LIU ZHAOZHI on 2017-6-21.
 */
@Service
public class JedisAdapter implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool pool;
    public static void print(int index, Object obj){
        System.out.println(String.format("%d, %s",index, obj.toString()));
    }

    public static void main(String[] args){
        Jedis jedis = new Jedis("redis://localhost:6379/9");
        jedis.flushDB();
        jedis.set("hello","world");
        print(1,jedis.get("hello"));
        jedis.rename("hello","newhello");
        print(1,jedis.get("newhello"));
        jedis.setex("hello2",15,"world"); //15秒过期

        jedis.set("pv","100");
        jedis.incr("pv"); //加 1
        jedis.incrBy("pv",5);
        jedis.decrBy("pv",5);
        print(2,jedis.get("pv"));

        print(3,jedis.keys("*"));

        //list
        String listName = "list";
        jedis.del(listName);
        for(int i=0;i<10;i++){
            jedis.lpush(listName,"a"+String.valueOf(i));
        }
        print(4,jedis.lrange(listName,0,12));
        print(5,jedis.llen(listName));
        print(6,jedis.lpop(listName));
        print(7,jedis.llen(listName));
        print(8,jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER,"a4","xxx"));
        print(9,jedis.lrange(listName,0,12));

        //hash
        String userKey = "userxx";
        jedis.hset(userKey,"name","jim");
        jedis.hset(userKey,"age","24");
        jedis.hset(userKey,"phone","18817002313");
        print(12,jedis.hget(userKey,"name"));
        print(13,jedis.hgetAll(userKey));

        jedis.hdel(userKey,"phone");
        print(14,jedis.hgetAll(userKey));
        print(15,jedis.hexists(userKey,"email")); //判断是否存在
        print(16,jedis.hkeys(userKey));
        print(17,jedis.hvals(userKey));

        jedis.hsetnx(userKey,"school","lushan"); //不存在就加一个，存在无操作
        print(18,jedis.hgetAll(userKey));

        //set
        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        for(int i=0;i<10;i++){
            jedis.sadd(likeKey1,String.valueOf(i));
            jedis.sadd(likeKey2,String.valueOf(i*i));
        }
        print(20,jedis.smembers(likeKey1));
        print(21,jedis.smembers(likeKey2));
        print(22,jedis.sunion(likeKey1,likeKey2));
        print(22,jedis.sdiff(likeKey1,likeKey2));
        print(22,jedis.sinter(likeKey1,likeKey2));
        print(22,jedis.sismember(likeKey1,"12"));

        jedis.srem(likeKey1,"5");
        print(27,jedis.smembers(likeKey1));
        jedis.smove(likeKey2,likeKey1,"25");
        print(28,jedis.smembers(likeKey1));
        print(29,jedis.scard(likeKey1));  //统计个数

        //优先队列
        String rankKey = "rankKey";
        jedis.zadd(rankKey,15,"tom");
        jedis.zadd(rankKey,46,"tony");
        jedis.zadd(rankKey,23,"hera");
        jedis.zadd(rankKey,67,"lee");
        jedis.zadd(rankKey,87,"jack");
        print(30,jedis.zcard(rankKey));
        print(31,jedis.zcount(rankKey,61,100));
        print(32,jedis.zscore(rankKey,"tony"));
        jedis.zincrby(rankKey,2,"tony");
        print(33,jedis.zscore(rankKey,"tony"));
        jedis.zincrby(rankKey,2,"qqq");
        print(33,jedis.zscore(rankKey,"qqq"));  //不存在默认为0
        print(32,jedis.zrange(rankKey,0,100));
        print(34,jedis.zrange(rankKey,0,10));  //从小到大排序
        print(34,jedis.zrevrange(rankKey,0,10));  //上面反过来
        for(Tuple tuple:jedis.zrangeByScoreWithScores(rankKey,60,100)){
            print(37,tuple.getElement()+":"+String.valueOf(tuple.getScore()));
        }

        print(38,jedis.zrank(rankKey,"tony")); //排名
        print(38,jedis.zrevrank(rankKey,"tony"));

        String setKey = "zset";
        jedis.zadd(setKey,1,"a");
        jedis.zadd(setKey,1,"b");
        jedis.zadd(setKey,1,"c");
        jedis.zadd(setKey,1,"d");
        jedis.zadd(setKey,1,"e");
        jedis.zadd(setKey,1,"f");

        print(39,jedis.zlexcount(setKey,"-","+"));
        print(39,jedis.zlexcount(setKey,"[b","[d"));
        print(39,jedis.zlexcount(setKey,"(b","[d"));

        jedis.zrem(setKey,"b");
        print(40,jedis.zrange(setKey,0,10));

        jedis.zremrangeByLex(setKey,"(c","+");
        print(41,jedis.zrange(setKey,0,2));

//        JedisPool pool = new JedisPool();
//        for(int i=0;i<100;i++){
//            Jedis j = pool.getResource();
//            print(42,j.get("pv"));
//            j.close();
//        }

        //缓存
        User user = new User();
        user.setName("xx");
        user.setPassword("ppp");
        user.setHeadUrl("a.png");
        user.setSalt("salt");
        user.setId(1);
        print(46,JSONObject.toJSONString(user));
        jedis.set("user1", JSONObject.toJSONString(user));  //存进redis

        String value = jedis.get("user1");
        User user2 = JSON.parseObject(value,User.class);   //从redis取出
        print(47,user2);

        int k=2;

        try {
            Transaction tx = jedis.multi();
            tx.zadd("qq", 2, "1");
            tx.zadd("qq2", 3, "2");
            List<Object> objs = tx.exec();
            tx.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        k = 2;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://localhost:6379/10");
    }

    public long sadd(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sadd(key,value);
        }catch (Exception e){
            logger.error("发生异常："+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }

    public long srem(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.srem(key,value);
        }catch (Exception e){
            logger.error("发生异常："+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("发生异常："+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }

    public boolean sismember(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key,value);
        }catch (Exception e){
            logger.error("发生异常："+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return false;
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public List<String> lrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long zadd(String key, double score, String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key, score, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long zrem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Set<String> zrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Set<String> zrevrange(String key, int start, int end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    //计算队列里有多少数字
    public long zcard(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    //判断分子有没有
    public Double zscore(String key, String member){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key,member);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Jedis getJedis(){
        return pool.getResource();
    }

    //开启事务
    public Transaction multi(Jedis jedis){
        try{
            return jedis.multi();
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }
        return null;
    }

    //执行事务
    public List<Object> exec(Transaction tx, Jedis jedis) {
        try {
            return tx.exec();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            tx.discard();
        } finally {
            if (tx != null) {
                try {
                    tx.close();
                } catch (IOException e) {
                    logger.error("发生异常" + e.getMessage());
                }
            }
            if(jedis!=null){
                jedis.close();
            }
        }
        return null;
    }
}
