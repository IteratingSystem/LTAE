package org.ltae.manager;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @Auther WenLong
 * @Date 2025/6/17 11:11
 * @Description 页面管理器
 **/
public class ScreenManager {
    private static ObjectMap<Class<? extends Screen> , Screen> table;

    private static ObjectMap<Class<? extends Screen> , Screen> getTable(){
        if (table == null){
            table = new ObjectMap<>();
        }
        return table;
    }

    public static void put(Screen screen){
        getTable().put(screen.getClass(),screen);
    }
    public static Screen getScreen(Class<? extends Screen> zClass){
        return getTable().get(zClass);
    }
}
