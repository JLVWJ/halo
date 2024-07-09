package com.lvwj.halo.dubbo.serializer;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Long,BigInteger,BigDecimal自动序列化为String，避免前端丢失精度
 *
 * 例：Long,BigInteger: 36477751321993216 => "36477751321993216"
 *         BigDecimal: 25.00 => "25.00"，如果不转String, ".00"会被截掉，如果产品要求展示“.00” 需和前端约定好 小数点统一返回字符串。
 *
 * @author lvweijie
 * @date 2024年01月24日 12:21
 */
public class BigNumberSerializer implements ISerializer<Number> {
    @Override
    public Object serialize(Object number) {
        if (number instanceof Long || number instanceof BigInteger || number instanceof BigDecimal) { //和前端约定好的情况下，可加上"|| number instanceof BigDecimal"
            return number.toString();
        }
        return number;
    }
}
