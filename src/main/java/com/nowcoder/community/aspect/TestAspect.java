package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Aspect
//@Component
public class TestAspect {
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void cutPoint(){

    }

    @Before("cutPoint()")
    public void before(){
        System.out.println("before");
    }

    @After("cutPoint()")
    public void after(){
        System.out.println("after");
    }

    @Around("cutPoint()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("执行前");
        Object ob = joinPoint.proceed();
        System.out.println("执行后");
        return ob;
    }
}
