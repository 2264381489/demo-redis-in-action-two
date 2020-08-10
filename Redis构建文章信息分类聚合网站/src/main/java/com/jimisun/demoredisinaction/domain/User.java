/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: User
 * Author:   jimisun
 * Date:     2018-11-22 16:17
 * Description: 用户实体
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jimisun.demoredisinaction.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 〈一句话功能简述〉<br>
 * 〈用户实体〉
 *
 * @author jimisun
 * @create 2018-11-22
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class User {
    private Integer userId;
    private String username;
    private String password;
}
