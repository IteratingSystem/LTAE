package org.ltae.manager;

import com.badlogic.gdx.Gdx;
import org.ltae.LtaePlugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
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
    public static Class<Object> ROOT_CLASS_GAME;
    // 引擎项目的根类
    public final static Class<LtaePlugin> ROOT_CLASS_ENGINE =  LtaePlugin.class;

    private static Class<Object> getGameRootClass() {
        if (ROOT_CLASS_GAME == null) {
            Gdx.app.error(TAG, "Game root class is null,Please set 'ROOT_CLASS_GAME' from 'ReflectionManager'");
            return null;
        }
        return ROOT_CLASS_GAME;
    }

    public static Reflections getReflections(Class<Object> rootClass){
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
    public static <T> Set<Class<? extends T>> getSubTypesOf(Class<Object> rootClass, Class<T> type) {
        return getReflections(rootClass).getSubTypesOf(type);
    }
}
