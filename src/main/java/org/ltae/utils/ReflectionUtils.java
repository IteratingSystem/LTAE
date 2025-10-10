package org.ltae.utils;

import com.artemis.Component;
import com.badlogic.gdx.Gdx;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:19
 * @Description 反射工具
 **/
public class ReflectionUtils {
    private static final String TAG = ReflectionUtils.class.getSimpleName();

    public static <T> T createObject(String className, Class<?>[] paramTypes, Object[] paramValues, Class<T> expectedType) {
        try {
            Class<?> clazz = Class.forName(className);
            if (!expectedType.isAssignableFrom(clazz)) {
                throw new ClassCastException("Class " + className + " is not a subtype of " + expectedType.getName());
            }

            Constructor<?> constructor = clazz.getDeclaredConstructor(paramTypes);
            @SuppressWarnings("unchecked")
            T instance = (T) expectedType.cast(constructor.newInstance(paramValues));
            return instance;
        } catch (Exception e) {
            Gdx.app.error(TAG, "Failed to createInstance: " + className);
            throw new RuntimeException(e);
        }
    }

    /**
     * 简化版：无参构造
     */
    public static <T> T createObject(String className,Class<T> expectedType){
        return createObject(className, new Class<?>[0], new Object[0],expectedType);
    }

    /**
     * 查找包内所有指定类型的类
     * @param packages
     * @param cLass
     * @return
     * @param <T>
     */
    public static <T> Set<Class<? extends T>> getClasses(String[] packages,Class<? extends T> cLass){
        Set<Class<? extends T>> classes = new HashSet<>();
        for (String pkg : packages) {
            Reflections reflections = new Reflections(pkg);
            classes.addAll(reflections.getSubTypesOf(cLass));
        }
        return classes;
    }
}
