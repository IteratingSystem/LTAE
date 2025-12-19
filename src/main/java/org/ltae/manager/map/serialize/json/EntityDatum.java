package org.ltae.manager.map.serialize.json;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther WenLong
 * @Date 2025/7/4 10:26
 * @Description 实体数据
 **/
public class EntityDatum {
    public int entityId;
    public int mapObjectId;
    //地图名称
    public String fromMap;
    public String name;
    public String type;
    public List<CompDatum> components;
    public EntityDatum(){}

    public boolean hasComp(String compName){
        if (components == null){
            components = new ArrayList<>();
        }
        for (CompDatum component : components) {
            if (component.name.equals(compName)) {
                return true;
            }
        }
        return false;
    }
    public CompDatum getCompJson(String compName){
        if (!hasComp(compName)) {
            Gdx.app.error(getTag(),"Failed to getCompJson,is not has component in components,compName:"+compName);
            return null;
        }
        for (CompDatum component : components) {
            if (component.name.equals(compName)) {
                return component;
            }
        }
        return null;
    }
    private String getTag(){
        return getClass().getSimpleName();
    }
    public boolean equals(EntityDatum entityDatum){
        return entityDatum.fromMap.equals(fromMap)  && entityDatum.mapObjectId == mapObjectId;
    }
}
