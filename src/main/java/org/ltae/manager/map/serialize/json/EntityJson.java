package org.ltae.manager.map.serialize.json;

import com.artemis.io.SaveFileFormat;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;

/**
 * @Auther WenLong
 * @Date 2025/7/4 10:26
 * @Description
 **/
public class EntityJson{
    public int entityId;
    public int mapObjectId;
    public String name;
    public String type;
    public Bag<ComponentJson> components;
    public EntityJson(){}

    public boolean hasComp(String compName){
        if (components == null){
            components = new Bag<>();
        }
        for (ComponentJson component : components) {
            if (component.name.equals(compName)) {
                return true;
            }
        }
        return false;
    }
    public ComponentJson getCompJson(String compName){
        if (!hasComp(compName)) {
            Gdx.app.error(getTag(),"Failed to getCompJson,is not has component in components,compName:"+compName);
            return null;
        }
        for (ComponentJson component : components) {
            if (component.name.equals(compName)) {
                return component;
            }
        }
        return null;
    }
    private String getTag(){
        return getClass().getSimpleName();
    }
}
