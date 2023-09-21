package com.nowcoder.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Aspect
@Component
//切片 用于记录日志 xxx用户在xxx时刻访问了xxx资源
public class ServiceLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    //对服务层中的数据进行切入 当用户访问时 记录其访问日志流程
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void cutPoint(){

    }

    @Before("cutPoint()")
    public void before(JoinPoint joinPoint){
        //需要记录的日志信息为：
        // 用户[1.2.3.4](为用户IP地址),在[xxx](时间),访问了[com.nowcoder.community.service.xxx()].(什么方法)
        //获取访问用户的ip地址
        ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attribute == null) {
            return;
        }
        HttpServletRequest request = attribute.getRequest();
        String IP = request.getRemoteHost();

        //获取访问的时间
        String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());

        //获取访问的方法
        String typeName = joinPoint.getSignature().getDeclaringTypeName();//获取包名和类名
        String MethodName = joinPoint.getSignature().getName();//获取方法名
        String target = typeName + "." + MethodName;//拼接起来访问的全类名方法
        //输出日志信息
        //logger.info(String.format("用户[%s],在[%s],访问了[%s].", IP, time, target));
    }
}
