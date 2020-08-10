/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: Item
 * Author:   jimisun
 * Date:     2018-11-24 15:08
 * Description: 商品实体
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
 * 〈商品实体〉
 *
 * @author jimisun
 * @create 2018-11-24
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private String productName;
    private String intro;
    private Integer Number;

//    public Item(String productName, String intro, Integer number) {
//        this.productName = productName;
//        this.intro = intro;
//        Number = number;
//    }

//    public Item() {
//
//    }
}
