package org.ltae.singleton;

import com.badlogic.gdx.utils.Json;

/**
 * @Auther WenLong
 * @Date 2025/2/11 16:23
 * @Description
 **/
public class JsonManager {
    private static Json instance;

    private JsonManager() {}
    public static Json getInstance() {
        if (instance == null) {
            instance = new Json();
        }
        return instance;
    }
}
