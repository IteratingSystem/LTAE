package org.ltae.manager.map;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.serialize.data.EntityData;
import org.ltae.serialize.data.Properties;

public class WorldState {
    public String curtMap;
    public ObjectMap<String, EntityData> entityData;
    public ObjectMap<String, Properties> systemProps;  // 改为使用类名作为key
    public WorldState(){
        entityData = new ObjectMap<>();
        systemProps = new ObjectMap<>();
    }
}
