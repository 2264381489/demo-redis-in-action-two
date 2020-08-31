package com.jimisun.controller;

import com.jimisun.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@Controller
@RequestMapping("/cookie")
public class Login {
    @Autowired
    Jedis jedis;

    //注册
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(User user,Model model) {


        if (jedis.exists(user.getUsername())) {
            return "fail";
        }
        //设置用户
        jedis.set("Userlist" + user.getUsername(), user.getPassword());
//        jedis.set()
        model.addAttribute("result", "注册成功!");
        return "RegAndLog";
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(User user, HttpServletRequest req, Model model) {
        if (jedis.get("Userlist"+user.getUsername()).equals(user.getPassword())) {
            //更新url
            String url = getURL(req);
            String s = jedis.get("userIP" + user.getUsername());
            jedis.set("userIP"+user.getUsername(),url);
            model.addAttribute("result","欢迎入内");
            return "RegAndLog";
        }
        model.addAttribute("result","密码错误");
        return "RegAndLog";
    }

    public String getURL(HttpServletRequest req) {
       String uri = req.getRequestURI();
        return uri;
    }
}