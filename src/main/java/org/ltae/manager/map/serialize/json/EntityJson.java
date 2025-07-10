package org.ltae.manager.map.serialize.json;

import com.artemis.io.SaveFileFormat;
import com.artemis.utils.Bag;

/**
 * @Auther WenLong
 * @Date 2025/7/4 10:26
 * @Description
 **/
public class EntityJson extends SaveFileFormat {
    public int entityId;
    public int mapObjectId;
    public String name;
    public String type;
    public Bag<ComponentJson> components;
    public EntityJson(){}

    public boolean hasComp(String name){
        if (components == null){
            components = new Bag<>();
        }
        for (ComponentJson component : components) {
            if (component.name.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
