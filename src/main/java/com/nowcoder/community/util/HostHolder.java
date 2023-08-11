package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

//使用ThreadLocal持有用户信息  用于替代session
//为什么要用这个？
/*
* 因为在我们的应用中存在多个请求同时访问同一个用户的情况 即多线程并发访问
* 每个不同的并发请求可以视为一个独立的线程，通过ThreadLocal来实现数据的隔离和共享，从而避免了多线程环境下数据共享和数据不一致的问题。
* */
/*
*       为什么不用session来存放用户信息？
* 但在多线程环境下，使用session来存放用户信息可能会引发线程安全问题。这是因为session是共享的，多个线程可以同时访问和修改session中的数据。
* 当多个线程同时修改session中的用户信息时，会出现数据不一致的问题。
* 另外，session的生命周期通常是与用户的会话绑定的，当用户关闭浏览器或者超过一定时间没有活动时，session会被销毁。
* 这意味着如果一个用户的请求在多个线程中处理，那么在不同的线程中可能无法获取到该用户的session信息。
* 相比之下，使用ThreadLocal<User>来存放用户信息可以解决上述问题。
* 每个线程都会有独立的ThreadLocal副本，不同线程之间的数据互不干扰，从而避免了线程安全问题。
* 而且ThreadLocal<User>的生命周期与线程绑定，可以确保在同一个线程中始终能够获取到正确的用户信息。
* 因此，当需要在多线程环境下存放用户信息时，使用ThreadLocal<User>比使用session更为合适。
* 但需要注意的是，ThreadLocal<User>只在当前线程中有效，无法在不同线程之间共享数据。
* 如果需要在不同线程之间共享数据，可以考虑使用其他线程安全的数据结构或者同步机制。
* */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    //向
    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
