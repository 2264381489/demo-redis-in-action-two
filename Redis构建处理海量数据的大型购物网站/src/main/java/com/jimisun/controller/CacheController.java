/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CacheReqeustController
 * Author:   jimisun
 * Date:     2018-11-24 15:58
 * Description: 测试缓存网页的控制器
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jimisun.controller;

import com.google.gson.Gson;
import com.jimisun.domain.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import redis.clients.jedis.Jedis;

import java.util.Random;

/**
 * 〈一句话功能简述〉<br>
 * 〈测试缓存网页的控制器〉
 *
 * @author jimisun
 * @create 2018-11-24
 * @since 1.0.0
 */
@RequestMapping("/cache")
@Controller
public class CacheController {

    @Autowired
    private Jedis jedis;


    @RequestMapping("/testCacheForItem/{itemname}")
    public String testCacheForItem(Model model, @PathVariable(required = true, name = "itemname") String itemname) {
        /*模拟数据*/
        Item item = new Item(itemname, itemname + "这是商品的介绍" + itemname, new Random().nextInt(10));
        /*判断是否被缓存*/
        Boolean hexists = jedis.exists("cache:" + itemname);
        if (!hexists) {
            Gson gson = new Gson();
            String s = gson.toJson(item);
            jedis.set("cache:" + itemname, s);
            model.addAttribute("s", s);
            model.addAttribute("result", "第一次访问,已经加入Redis缓存");
            return "CacheItem";
        }
        String s = jedis.get("cache:" + itemname);
        model.addAttribute("s", s);
        model.addAttribute("result", "重复访问,从Redis中读取数据");
        return "CacheItem";
    }
}
