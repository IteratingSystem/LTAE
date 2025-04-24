package org.ltae.utils;

import com.badlogic.gdx.Gdx;

/**
 * @Auther WenLong
 * @Date 2025/3/18 16:54
 * @Description 输入工具
 **/
public class InputUtils {
    //禁用输入
    public static boolean DISABLE = false;

    public static boolean isKeyPressed(int key){
        if (DISABLE){
            return false;
        }

        return Gdx.input.isKeyPressed(key);
    }
    public static boolean isKeyPressed(int[] keys){
        if (DISABLE){
            return false;
        }
        for (int key : keys) {
            if (!Gdx.input.isKeyPressed(key)) {
                return false;
            }
        }
        return true;
    }
}
