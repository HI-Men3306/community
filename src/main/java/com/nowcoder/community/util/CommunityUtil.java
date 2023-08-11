package com.nowcoder.community.util;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

//用于生成一些常规的数据  例如随机码   MD5加密
public class CommunityUtil {

    //随机生成字符串
    public static String GenerateUUID(){
        //随机生成一段字符串 并将其中的-去除
        return UUID.randomUUID().toString().replace("-","");
    }

    //MD5加密
    //不能只对给出的密码进行加密   为更加安全  在其后面添加上一段随机字符串一起加密
    public static String md5(String key){
        if(StringUtils.isBlank(key)){//对传输过来的key进行判断
            return null;
        }
        //对key进行MD5加密
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    //根据传入的参数生成json串
    //响应状态码  0：正常   1：异常
    public static String getJSONString(int code, String msg, Map<String, Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map != null){
            for(String key : map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toString();
    }
    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }
}
