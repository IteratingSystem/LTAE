package org.ltae.manager.map;

import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.manager.map.serialize.json.EntityData;

public class WorldState {
    public String curtMap;
    public ObjectMap<String, EntityData> entityData;
    public WorldState(){
        entityData = new ObjectMap<>();
    }
}
