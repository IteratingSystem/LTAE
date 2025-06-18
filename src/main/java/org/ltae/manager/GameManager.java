package org.ltae.manager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @Auther WenLong
 * @Date 2025/6/17 11:11
 * @Description 页面管理器
 **/
public class GameManager {
    private final static String TAG = GameManager.class.getSimpleName();
    private static ObjectMap<Class<? extends Game> , Game> table;

    private static ObjectMap<Class<? extends Game> , Game> getTable(){
        if (table == null){
            table = new ObjectMap<>();
        }
        return table;
    }

    public static void register(Game game){
        getTable().put(game.getClass(),game);
    }
    public static Game getGame(Class<? extends Game> zClass){
        ObjectMap<Class<? extends Game>, Game> table = getTable();
        if (!table.containsKey(zClass)) {
            Gdx.app.error(TAG,"This Game is not register in GameManager:"+zClass.getSimpleName());
            return null;
        }
        return table.get(zClass);
    }
}
