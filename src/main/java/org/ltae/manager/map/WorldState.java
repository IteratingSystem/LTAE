package org.ltae.manager.map;

import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.serialize.data.Properties;

public class WorldState {
    public String curtMap;
    public ObjectMap<String, String> entityDataJson;
    public ObjectMap<String, Properties> systemProps;
    public WorldState(){
        entityDataJson = new ObjectMap<>();
        systemProps = new ObjectMap<>();
    }
}
