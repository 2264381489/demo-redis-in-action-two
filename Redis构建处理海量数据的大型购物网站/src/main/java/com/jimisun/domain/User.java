/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: User
 * Author:   jimisun
 * Date:     2018-11-24 12:26
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jimisun.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author jimisun
 * @create 2018-11-24
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String username;
    private String password;
}
