package org.ltae.manager.map.serialize.json;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther WenLong
 * @Date 2025/7/4 10:26
 * @Description
 **/
public class EntityData {
    public int entityId;
    public int mapObjectId;
    public String fromMap;
    public String name;
    public String type;
    public List<CompData> components;
    public EntityData(){}

    public boolean hasComp(String compName){
        if (components == null){
            components = new ArrayList<>();
        }
        for (CompData component : components) {
            if (component.name.equals(compName)) {
                return true;
            }
        }
        return false;
    }
    public CompData getCompJson(String compName){
        if (!hasComp(compName)) {
            Gdx.app.error(getTag(),"Failed to getCompJson,is not has component in components,compName:"+compName);
            return null;
        }
        for (CompData component : components) {
            if (component.name.equals(compName)) {
                return component;
            }
        }
        return null;
    }
    private String getTag(){
        return getClass().getSimpleName();
    }
    public boolean equals(EntityData entityData){
        return entityData.fromMap.equals(fromMap)  && entityData.mapObjectId == mapObjectId;
    }
}
