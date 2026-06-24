package org.ltae.manager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @Auther WenLong
 * @Date 2025/6/17 11:11
 * @Description 游戏管理器
 **/
public class GameManager {
    private final static String TAG = GameManager.class.getSimpleName();
    private static ObjectMap<Class<? extends Game> , Game> table;
    private static Game current;

    private static ObjectMap<Class<? extends Game> , Game> getTable(){
        if (table == null){
            table = new ObjectMap<>();
        }
        return table;
    }

    public static void register(Game game){
        if (getTable().containsKey(game.getClass())) {
            return;
        }
        getTable().put(game.getClass(),game);
    }
    public static void setCurrent(Game game){
        current =  game;
        register(game);
    }
    public static Game getCurrent(){
        if (current == null) {
            Gdx.app.error(TAG,"Failed to get current game,Current game game is null,Please first use 'setCurrent' to set the current game");
        }
        return current;
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
