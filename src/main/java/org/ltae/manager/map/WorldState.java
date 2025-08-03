package org.ltae.manager.map;

import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.manager.map.serialize.json.EntityDataList;

public class WorldState {
    public String curtMap;
    public ObjectMap<String, EntityDataList> entityData;
    public WorldState(){
        entityData = new ObjectMap<>();
    }
}
