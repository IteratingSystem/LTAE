package org.ltae.serialize.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

/**
 * @Auther WenLong
 * @Date 2025/7/4 10:26
 * @Description 实体数据
 **/
public class EntityDatum {
    //在构造实体数据的过程中,可以通过地图对象和游戏世界两种方式构造,此属性记录构造数据的来源
    public int dataFrom;
    //在实体存档过后,用于记录此id,由于所有实体重建过程中,id会重新生成,故而需要此属性
    public int lastId;

    public int entityId;
    public int mapObjectId;
    //地图名称
    public String fromMap;
    public String name;
    public String type;
    public Array<CompMirror> compMirrors;
    public EntityDatum(){
        lastId = -1;
    }

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
