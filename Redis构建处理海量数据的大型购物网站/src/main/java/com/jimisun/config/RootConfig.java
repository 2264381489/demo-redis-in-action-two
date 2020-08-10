/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: RootConfig
 * Author:   jimisun
 * Date:     2018-11-24 12:00
 * Description: 配置Sprng
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jimisun.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

/**
 * 〈一句话功能简述〉<br>
 * 〈配置Sprng〉
 *
 * @author jimisun
 * @create 2018-11-24
 * @since 1.0.0
 */
@Configuration
public class RootConfig {


    @Bean
    public Jedis jedis() {
        return new Jedis();
    }


}
