/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ArticleController
 * Author:   jimisun
 * Date:     2018-11-22 16:23
 * Description: 文章实体控制器
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jimisun.demoredisinaction.controller;

import com.jimisun.demoredisinaction.domain.Article;
import com.jimisun.demoredisinaction.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ZParams;

import java.util.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈文章实体控制器〉
 *
 * @author jimisun
 * @create 2018-11-22
 * @since 1.0.0
 */
@Controller
@RequestMapping("/article")
public class ArticleController {

    private static final int ONE_WEEK_IN_SECONDS = 7 * 10; //为了测试方便将原先的一周换为70s
    private static final int VOTE_SCORE = 432;
    private static final int ARTICLES_PER_PAGE = 8;


    @Autowired
    private Jedis jedis;


    /**
     * 获取分组文章列表
     *
     * @return
     */
    @GetMapping(value = "/getGroupArticleList")
    public String getGroupArticle(@RequestParam(required = false, defaultValue = "1") Integer page,
                                  @RequestParam(required = false, defaultValue = "score") String order,
                                  @RequestParam(required = true) String group, Model model) {
        List<Article> groupArticles = getGroupArticles(jedis, group, page, order + ":");
        /*拼装分页信息*/
        model.addAttribute("groupArticles", groupArticles);
        model.addAttribute("group", group);
        model.addAttribute("order", order);
        model.addAttribute("page", page);
        return "group";
    }


    /**
     * 获得所有文章列表文章列表
     *
     * @param page  页数
     * @param order 排序
     * @param model 模型
     * @return
     */
    @GetMapping(value = "/getArticleList")
    public String getArticle(@RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "score") String order, Model model) {
        /*获取文章列表*/
        List<Article> articleArrayList = getArticles(jedis, page, order + ":");
        /*拼装分页信息*/
        model.addAttribute("articleArrayList", articleArrayList);
        model.addAttribute("order", order);
        model.addAttribute("page", page);
        return "index";
    }


    /**
     * 投票
     *
     * @param articleId
     * @param model
     * @return
     */
    @GetMapping(value = "/articleVote")
    public String articleVote(Integer articleId, int tag, RedirectAttributes model) {
        /*模拟用户*/
        User user = getUser();
        String result = article_vote(jedis, user, articleId, tag);

        /*设置模型数据*/
        model.addFlashAttribute("result", result);
        model.addFlashAttribute("page", 1);
        model.addFlashAttribute("order", "score");
        return "redirect:/article/getArticleList";
    }


    /**
     * 发布文章：
     * 从前端获取article
     * 标题，所属链接，所属组，放进redis中，并且设置失效时间
     *
     * @param article
     * @param model
     * @return
     */
    public String addArticle1(Article article, Model model){
           //首先获得虚拟用户
        User user=getUser();
//        去调用redis将前端传过来的内容放入redis中,但是这个应该是放在一个map中，因为article有关的各种数据同属一个属性。
        Map<String,String> articleMap=new HashMap<>();
        //对时间进行设置
        long now = System.currentTimeMillis() / 1000;
        articleMap.put("title",article.getTitle());
        articleMap.put("link",article.getLink());
        articleMap.put("group",article.getGroup());
        articleMap.put("user",user.getUserId().toString());
        articleMap.put("time",String.valueOf(now));
        articleMap.put("votes","1");
        articleMap.put("opposeVotes", "0");

//        redis插入，应该将每一个文章单独列成一个redis中的hset，这样才行
        //最好使用自增id
        //这个自增id是用一个string存上的。
        String id = String.valueOf(jedis.incr("article:"));
        String articleId="article:"+id;
        jedis.hmset(articleId,articleMap);


        /*将文章的发帖人本人添加至投票列表*/
        String voted = "voted:" + id;
        jedis.sadd(voted, user.getUserId().toString());
        jedis.expire(voted, ONE_WEEK_IN_SECONDS);

        /*将文章加入其所属分组*/
        addGroup(jedis, articleId, new String[]{article.getGroup()});
        /*设置模型数据*/
        model.addAttribute("page", 1);//分页
        model.addAttribute("order", "score");//order
        return "redirect:/article/getArticleList";

    }

    /**
     * 发布文章
     *
     * 实现功能：
     * 添加除
     *
     *
     * @param article
     * @param model
     * @return
     */
    @PostMapping(value = "/addArticle")
    public String addArticle(Article article, Model model) {
        /*模拟用户*/
        User user = getUser();

        /*从Redis获取文章的自增ID*/
        String id = String.valueOf(jedis.incr("article:"));

        /*将文章的发帖人本人添加至投票列表*/
        String voted = "voted:" + id;
        jedis.sadd(voted, user.getUserId().toString());
        jedis.expire(voted, ONE_WEEK_IN_SECONDS);

        /*将文章添加至文章列表*/
        long now = System.currentTimeMillis() / 1000;
        String articleId = "article:" + id;
        HashMap<String, String> articleData = new HashMap<>();
        articleData.put("title", article.getTitle());
        articleData.put("link", article.getLink());
        articleData.put("user", user.getUserId().toString());
        articleData.put("time", String.valueOf(now));
        articleData.put("votes", "1");
        articleData.put("group", article.getGroup());
        articleData.put("opposeVotes", "0");
        jedis.hmset(articleId, articleData);

        /*将文章添加至分值列表*/
        jedis.zadd("score:", now + VOTE_SCORE, articleId);
        /*将文章添加至发布时间列表*/
        jedis.zadd("time:", now, articleId);


        /*将文章加入其所属分组*/
        addGroup(jedis, articleId, new String[]{article.getGroup()});

        /*设置模型数据*/
        model.addAttribute("page", 1);
        model.addAttribute("order", "score");
        return "redirect:/article/getArticleList";
    }


    /*-----------------------------------------------------------------------------------------------------------------*/


    private static String article_vote(Jedis jedis, User user, Integer articleId, int tag) {

        /*判断文章是否还能投支持票（redis里面支持票的SET数据结构）*/
        long cutoff = (System.currentTimeMillis() / 1000) - ONE_WEEK_IN_SECONDS;
        if (jedis.zscore("time:", "article:" + articleId) < cutoff) {
            return "文章已经太久远，不再提供投票";
        }

        /*判断当前用户是否已经投票*/
        if (jedis.sismember("voted:" + articleId, user.getUserId().toString()) ||
                jedis.sismember("novoted:" + articleId, user.getUserId().toString())) {
            return "您已经投过票啦！";
        }

        /*投票*/
        if (tag == 0) {
            jedis.sadd("voted:" + articleId, user.getUserId().toString());
            /*更新文章的分值*/
            jedis.zincrby("score:", VOTE_SCORE, "article:" + articleId);
            /*更新文章的支持票人数*/
            jedis.hincrBy("article:" + articleId, "votes", 1);
        } else if (tag == 1) {
            jedis.sadd("novoted:" + articleId, user.getUserId().toString());
            /*更新文章的分值*/
            jedis.zincrby("score:", -VOTE_SCORE, "article:" + articleId);
            /*更新文章的反对票人数*/
            jedis.hincrBy("article:" + articleId, "opposeVotes", 1);
        }
        return "投票成功！";


    }


    private static List<Article> getGroupArticles(Jedis jedis, String group, int page) {
        return getGroupArticles(jedis, group, page, "score:");
    }

    private static List<Article> getGroupArticles(Jedis jedis, String group, int page, String order) {
        String key = order + group;
        if (!jedis.exists(key)) {
            ZParams params = new ZParams().aggregate(ZParams.Aggregate.MAX);
            jedis.zinterstore(key, params, "group:" + group, order);
            jedis.expire(key, 60);
        }
        return getArticles(jedis, page, key);
    }


    private static List<Article> getArticles(Jedis jedis, Integer page) {
        return getArticles(jedis, page, "score");
    }

    private static List<Article> getArticles(Jedis jedis, Integer page, String order) {
        int start = (page - 1) * ARTICLES_PER_PAGE;
        int end = start + ARTICLES_PER_PAGE - 1;
//        返回有序集key中，指定区间内的成员。其中成员的位置按score值递减
        Set<String> ids = jedis.zrevrange(order, start, end);
        List<Article> articleList = new ArrayList<>();
        for (String id : ids) {
            Article article = new Article();
            /*从文章表中获取文章*/
            Map<String, String> stringStringMap = jedis.hgetAll(id);
            Integer userId = Integer.valueOf(stringStringMap.get("user"));
            article.setId(Integer.valueOf(id.substring(id.indexOf(':') + 1)));
            article.setUser(getUser());
            article.setLink(stringStringMap.get("link"));
            article.setVotes(Integer.valueOf(stringStringMap.get("votes")));
            article.setTime(stringStringMap.get("time"));
            article.setTitle(stringStringMap.get("title"));
            article.setGroup(stringStringMap.get("group"));
            article.setOpposeVotes(Integer.valueOf(stringStringMap.get("opposeVotes")));
            articleList.add(article);
        }
        return articleList;
    }


    private static void addGroup(Jedis jedis, String articleId, String[] toAdd) {
        for (String group : toAdd) {
            jedis.sadd("group:" + group, articleId);
        }
    }


    private static User getUser() {
        Integer i = new Random().nextInt(10000);
        return new User(i, "模拟用户" + i, "模拟用户" + i);
    }

}
