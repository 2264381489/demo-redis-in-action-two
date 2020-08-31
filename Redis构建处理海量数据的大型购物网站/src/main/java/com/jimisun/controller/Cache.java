package com.jimisun.controller;

import com.google.gson.Gson;
import com.jimisun.domain.Item;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import redis.clients.jedis.Jedis;

import javax.validation.Valid;
import java.util.Random;

@RequestMapping("/cache")
public class Cache {
    private Jedis jedis=new Jedis("localhost");
    //访问一个页面,看是否存在过
    //1.访问接口是什么?
    //test
    //2.捕获这个接口
    @GetMapping("/test/{name}")
    public String cache(@PathVariable("name")String name, Model model){
        /*模拟数据*/
        Item item = new Item(name, name + "这是商品的介绍" + name, new Random().nextInt(10));
   //是否被缓存
        /*判断是否被缓存*/
        Boolean hexists = jedis.exists("cache:" + name);
        if (!hexists) {
            Gson gson = new Gson();
            String s = gson.toJson(item);
            jedis.set("cache:" + name, s);
            model.addAttribute("s", s);
            model.addAttribute("result", "第一次访问,已经加入Redis缓存");
            return "CacheItem";
        }
        String s = jedis.get("cache:" + name);
        model.addAttribute("s", s);
        model.addAttribute("result", "重复访问,从Redis中读取数据");
        return "CacheItem";
    }
}
