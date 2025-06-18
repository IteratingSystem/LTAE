package org.ltae.manager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @Auther WenLong
 * @Date 2025/6/17 11:11
 * @Description 页面管理器
 **/
public class GameManager {
    private static ObjectMap<Class<? extends Game> , Game> table;

    private static ObjectMap<Class<? extends Game> , Game> getTable(){
        if (table == null){
            table = new ObjectMap<>();
        }
        return table;
    }

    public static void put(Game game){
        getTable().put(game.getClass(),game);
    }
    public static Game getScreen(Class<? extends Game> zClass){
        return getTable().get(zClass);
    }
}
