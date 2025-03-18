package org.ltae.utils;

import com.badlogic.gdx.Gdx;

/**
 * @Auther WenLong
 * @Date 2025/3/18 16:54
 * @Description 输入工具
 **/
public class InputUtils {
    public static boolean isKeyPressed(int key){
        return Gdx.input.isKeyPressed(key);
    }
    public static boolean isKeyPressed(int[] keys){
        for (int key : keys) {
            if (!Gdx.input.isKeyPressed(key)) {
                return false;
            }
        }
        return true;
    }
}
