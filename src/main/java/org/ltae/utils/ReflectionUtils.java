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
    /**
     * 通过类名和构造参数动态创建对象
     *
     * @param <T>          泛型类型
     * @param className    完整类名（如 "com.example.MyClass"）
     * @param paramTypes   构造参数类型数组（如 new Class[]{String.class, int.class}）
     * @param paramValues  构造参数值数组（如 new Object[]{"hello", 123}）
     * @return 创建的对象
     * @throws Exception 如果类不存在、构造器不匹配或实例化失败
     */
    public static <T> T createInstance(String className, Class<?>[] paramTypes, Object[] paramValues) {
        // 1. 加载类
        @SuppressWarnings("unchecked")
        T instance = null;
        try {
            Class<?> clazz = Class.forName(className);
            // 2. 获取匹配的构造器
            Constructor<?> constructor = clazz.getDeclaredConstructor(paramTypes);
            // 3. 设置构造器可访问（处理私有构造器）
            //constructor.setAccessible(true);
            // 4. 创建实例并返回
            instance = (T) constructor.newInstance(paramValues);
        } catch (ClassNotFoundException e) {
            Gdx.app.error(TAG,"Failed to createInstance,The specified class does not exist:"+className);
            throw new RuntimeException(e);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            Gdx.app.error(TAG,"Failed to createInstance;");
            throw new RuntimeException(e);
        }
        return instance;
    }

    /**
     * 简化版：无参构造
     */
    public static <T> T createInstance(String className){
        return createInstance(className, new Class<?>[0], new Object[0]);
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
