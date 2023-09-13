package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.nowcoder.community.util.RedisKeyUtil.getUVKey;


@Service
public class TotalDataService {
    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");

    //将指定的ip计入当日UV中
    public void recordUV(String ip){
        String redisKey = RedisKeyUtil.getUVKey(date.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey,ip);
    }

    //统计指定区间范围中的UV数量
    public long calculateUV(Date start,Date end){
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String key = RedisKeyUtil.getUVKey(date.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE, 1);
        }

        // 合并这些数据
        String redisKey = RedisKeyUtil.getUVKey(date.format(start), date.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());
        Long result = redisTemplate.opsForHyperLogLog().size(redisKey);
        redisTemplate.delete(redisKey);
        return result;
    }

    //将指定的用户id计入当日DAU中
    public void recordDAU(int userId){
        //redisKey对应的是当日的日期   而userId则是redisKey表中对应的下标
        //如果id为20的用户今天登录了  那么今天对应的redisKey中下标为20的值为true  否则为false
        String redisKey = RedisKeyUtil.getDAUKey(date.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey,userId,true);
    }

    //统计指定区间范围中的DAU数量
    public long calculateDAU(Date start,Date end){
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        //统计
        // 整理该日期范围内的key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String key = RedisKeyUtil.getDAUKey(date.format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE, 1);
        }

        // 进行OR运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(date.format(start), date.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(), keyList.toArray(new byte[0][0]));
                Long res = connection.bitCount(redisKey.getBytes());
                //删除合并的BitMap表
                redisTemplate.delete(redisKey);
                return res;
            }
        });
    }

}
