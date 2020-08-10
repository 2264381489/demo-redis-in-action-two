/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: Article
 * Author:   jimisun
 * Date:     2018-11-22 16:19
 * Description: 文章实体
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jimisun.demoredisinaction.domain;

import lombok.Data;

/**
 * 〈一句话功能简述〉<br>
 * 〈文章实体〉
 *
 * @author jimisun
 * @create 2018-11-22
 * @since 1.0.0
 */
@Data
public class Article {
    private Integer id;
    private String title;
    private String link;
    private User user;
    private String time;
    private Integer votes;
    private Integer opposeVotes;
    private String group;

}
