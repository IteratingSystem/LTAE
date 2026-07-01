package org.ltae.manager;

import com.badlogic.gdx.Gdx;
import org.ltae.LtaePlugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Set;


/**
 * 反射管理器
 *
 * <p></p>
 *
 * @author WenLong
 * @version 1.0.0
 * @date 2026/6/30 11:50
 * @see ReflectionManager
 */
public class ReflectionManager {
    public final static String TAG = ReflectionManager.class.getName();

    // 游戏项目的根类
    private static Class ROOT_CLASS_GAME;
    // 引擎项目的根类
    private final static Class ROOT_CLASS_ENGINE = LtaePlugin.class;
    // 单例
    private static ReflectionManager instance;

    // 单例限制构造器
    private ReflectionManager() {
        if (ROOT_CLASS_GAME == null) {
            Gdx.app.error(TAG, "Game root class is null,Please run 'setRootClass(Class<Object> ROOT_CLASS_GAME)' from 'ReflectionManager'");
            return;
        }
    }
    // 设置游戏项目根类,使用此管理器的游戏项目需要设置
    public static void setRootClass(Class ROOT_CLASS_GAME){
        ReflectionManager.ROOT_CLASS_GAME = ROOT_CLASS_GAME;
    }
    // 获取单例
    public static ReflectionManager getInstance() {
        if (instance == null) {
            instance = new ReflectionManager();
        }
        return instance;
    }


    // 获取游戏项目中所有继承于传入类型的类
    public <T> Set<Class<? extends T>> getSubTypesOfWithEngineAndGame(Class<T> type) {
        Set<Class<? extends T>> subTypesOfWithEngine = getSubTypesOfWithEngine(type);
        Set<Class<? extends T>> subTypesOfWithGame = getSubTypesOfWithGame(type);
        subTypesOfWithEngine.addAll(subTypesOfWithGame);
        return subTypesOfWithEngine;
    }
    // 获取游戏项目中所有继承于传入类型的类
    public <T> Set<Class<? extends T>> getSubTypesOfWithGame(Class<T> type) {
        return getSubTypesOf(ROOT_CLASS_GAME,type);
    }
    // 获取引擎项目中所有继承于传入类型的类
    public <T> Set<Class<? extends T>> getSubTypesOfWithEngine(Class<T> type) {
        return getSubTypesOf(ROOT_CLASS_ENGINE,type);
    }
    // 获取某个类目录及其子目录下所有的传入类型的类
    public <T> Set<Class<? extends T>> getSubTypesOf(Class rootClass, Class<T> type) {
        return getReflections(rootClass).getSubTypesOf(type);
    }

    // 通关类来得到包含其路径与子路径内所有类的反射工具示例
    public Reflections getReflections(Class rootClass){
        URL codeSource = rootClass.getProtectionDomain().getCodeSource().getLocation();
        return new Reflections(new ConfigurationBuilder()
                .addUrls(codeSource)
                .setScanners(
                        // 扫描所有子类型
                        Scanners.SubTypes.filterResultsBy(s -> true),
                        // 扫描类上的注解
                        Scanners.TypesAnnotated
                ));
    }

    // 通过class创建其对象
    public <T> T createObject(Class<T> clazz, Class<?>[] paramTypes, Object[] paramValues) {
        try {
            Constructor<?> constructor;

            // 1. 处理 null 或空数组：明确调用无参构造
            if (paramTypes == null || paramTypes.length == 0) {
                constructor = clazz.getDeclaredConstructor();
            } else {
                constructor = clazz.getDeclaredConstructor(paramTypes);
            }

            // 2. 暴力破解访问权限（解决 private/protected 问题）
            constructor.setAccessible(true);

            // 3. 处理 null 参数值
            Object[] args = (paramValues == null) ? new Object[0] : paramValues;

            @SuppressWarnings("unchecked")
            T instance = (T) constructor.newInstance(args);
            return instance;
        } catch (Exception e) {
            Gdx.app.error(TAG, "Failed to createInstance: " + clazz.getSimpleName(), e);
            throw new RuntimeException(e);
        }
    }
}
