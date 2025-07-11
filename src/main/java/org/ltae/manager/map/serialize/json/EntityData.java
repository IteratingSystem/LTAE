package org.ltae.manager.map.serialize.json;

import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;

/**
 * @Auther WenLong
 * @Date 2025/7/4 10:26
 * @Description
 **/
public class EntityData {
    public int entityId;
    public int mapObjectId;
    public String name;
    public String type;
    public Bag<ComponentData> components;
    public EntityData(){}

    public boolean hasComp(String compName){
        if (components == null){
            components = new Bag<>();
        }
        for (ComponentData component : components) {
            if (component.name.equals(compName)) {
                return true;
            }
        }
        return false;
    }
    public ComponentData getCompJson(String compName){
        if (!hasComp(compName)) {
            Gdx.app.error(getTag(),"Failed to getCompJson,is not has component in components,compName:"+compName);
            return null;
        }
        for (ComponentData component : components) {
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
