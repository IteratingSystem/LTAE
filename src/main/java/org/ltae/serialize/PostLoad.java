package org.ltae.serialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在方法上，WorldSerializationManager.load() 完成后自动调用。
 * 方法签名必须为: void methodName(World world)
 *
 * @author WenLong
 * @version 1.0.0
 * @date 2026/7/28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostLoad {
}
