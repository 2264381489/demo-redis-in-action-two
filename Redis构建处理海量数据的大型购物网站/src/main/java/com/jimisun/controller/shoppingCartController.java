package com.jimisun.controller;

import com.jimisun.domain.Item;
import com.jimisun.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * 购物车 --redis实现
 * 所要用到的接口:
 */
@Controller
@RequestMapping("/cart")
public class shoppingCartController {
    Jedis jedis = new Jedis("localhost", 6379);

    @GetMapping("/")
    public String goProductList() {
        return "ProductList";
    }

    //接受购买请求
    @RequestMapping("/addCart")
    public String addCart(Item item, Model model) {
        String key="shoppingcart:" + getUser("chendepeng", "123").getUsername();
        //1.购物车的实现--需要每个用户建立一个购物车,每个购物车分别行动
        //-->那么需要引入一个用户
        //用0与1对于增减进行判读
        //2.制作一个相关的逻辑,让方法可以通过同一条请求来完成两种不同操作.
        if (item.getNumber() > 0) {
//            String key="shoppingcart:" + getUser("chendepeng", "123").getUsername();
            if (jedis.exists(key))
            {
                jedis.hincrBy(key, item.getProductName(),1L);
            } else {
                jedis.hset(key, item.getProductName(), item.getNumber().toString());
            }
        } else {
            Integer integer = Integer.valueOf(jedis.hget(key, item.getProductName()));
            System.out.println(integer);
            if (integer>0){
                integer=integer-1;
                jedis.hset(key,item.getProductName(),String.valueOf(integer));
            }else {
                jedis.hdel(key, item.getProductName());
            }
            }
        //3.完成存储后,处理展示相关的问题
        Map<String, String> map = jedis.hgetAll("shoppingcart:" + getUser("chendepeng", "123").getUsername());


        //4.处理这些数据到前端
        model.addAttribute("result", "添加购物车成功!");
        //添加到页面中
        model.addAttribute("cartList", map);
        return "ProductList";
    }

    //引入一个用户
    public User getUser(String productname, String number) {
        User user = new User();
        user.setUsername(productname);
        user.setPassword(number);
        return user;
    }
}
