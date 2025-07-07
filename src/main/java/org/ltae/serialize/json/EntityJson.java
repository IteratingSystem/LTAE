package org.ltae.serialize.json;

import com.artemis.utils.Bag;
import com.badlogic.gdx.maps.MapObject;

/**
 * @Auther WenLong
 * @Date 2025/7/4 10:26
 * @Description
 **/
public class EntityJson {
    public int entityId;
    public String name;
    public String type;
    public Bag<ComponentJson> components;
    public MapObject mapObject;

//    public boolean hasComp(String name){
//        if (components == null){
//            components = new Bag<>();
//        }
//        for (ComponentJson component : components) {
//            if (component.name.equals(name)) {
//                return true;
//            }
//        }
//        return false;
//    }
}
