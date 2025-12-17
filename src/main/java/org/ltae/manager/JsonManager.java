package org.ltae.manager;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.Null;

/**
 * @Auther WenLong
 * @Date 2025/2/11 16:23
 * @Description
 **/
public class JsonManager {
    private static Json json;

    private JsonManager() {}
    public static Json getJson() {
        if (json == null) {
            json = new Json();
        }
        return json;
    }
    public static String toJson(Object object){
        return getJson().toJson(object);
    }
    public static @Null <T> T fromJson (Class<T> type, String json) {
        return  getJson().fromJson(type,json);
    }
    public static @Null <T> T fromJson (Class<T> type, FileHandle fileHandle) {
        return  getJson().fromJson(type,fileHandle);
    }
}
