/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: RootApplicationConfig
 * Author:   jimisun
 * Date:     2018-11-22 16:29
 * Description: SpringFreeMarker配置
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jimisun.demoredisinaction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

/**
 * 〈一句话功能简述〉<br>
 * 〈SpringFreeMarker配置〉
 *
 * @author jimisun
 * @create 2018-11-22
 * @since 1.0.0
 */
@Configuration
public class RootApplicationConfig {

    @Bean
    public Jedis getJedis() {
        return new Jedis();
    }

}
