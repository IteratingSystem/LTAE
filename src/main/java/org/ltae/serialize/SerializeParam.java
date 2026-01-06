package org.ltae.serialize;

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
public @interface SerializeParam {
//    boolean save() default true;
}
