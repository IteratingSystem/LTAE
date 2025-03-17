package org.ltae.tiled;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Auther WenLong
 * @Date 2025/2/13 14:58
 * @Description Tiled自定义类型挂载到对象后填入参数, 通过此注解自动赋值给组件
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TileParam {
    /**
     * 是否可以为空,主要用于日志输出提醒是否配置相关数据,可以为空时就算读取不到也不会提示日志
     * @return
     */
    boolean nullable() default false;
}
