package org.ltae.manager.map.serialize.json;

import com.artemis.utils.Bag;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.manager.JsonManager;

import java.io.Reader;

/**
 * @Auther WenLong
 * @Date 2025/7/4 10:27
 * @Description
 **/
public class ComponentJson {
    public String name;
    public Bag<PropertyJson> props;

    public <T> T get (String key,T defaultValue, Class<T> clazz) {
        for (PropertyJson prop : props) {
            if (prop.key.equals(key)) {
                return (T)prop.value;
            }
        }
        return defaultValue;
    }
    public boolean containsKey(String key){
        for (PropertyJson prop : props) {
            if (prop.key.equals(key)) {
                return true;
            }
        }
        return false;
    }
    public boolean containsKey(String[] keys){
        for (String key : keys) {
            if (!containsKey(key)) {
                return false;
            }
        }
        return true;
    }
}
