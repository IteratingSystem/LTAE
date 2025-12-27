package org.ltae.manager.map.serialize.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import org.ltae.serialize.CompMirror;

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
    public Array<CompMirror> compMirrors;
    public EntityDatum(){}

    public CompMirror getCompMirror(String compName){
        for (CompMirror compMirror : compMirrors) {
            if (compMirror.simpleName.equals(compName)) {
                return compMirror;
            }
        }
        Gdx.app.error(getTag(),"Failed to 'getCompMirror',not has 'compName': "+compName);
        return null;
    }
    public boolean hasComp(String compName){
        if (compMirrors == null){
            compMirrors = new Array<>();
        }
        for (CompMirror component : compMirrors) {
            if (component.simpleName.equals(compName)) {
                return true;
            }
        }
        return false;
    }
    public CompMirror getCompJson(String compName){
        if (!hasComp(compName)) {
            Gdx.app.error(getTag(),"Failed to getCompJson,is not has component in components,compName:"+compName);
            return null;
        }
        for (CompMirror component : compMirrors) {
            if (component.simpleName.equals(compName)) {
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
