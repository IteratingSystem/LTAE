package org.worldloom.manager.map;

import com.badlogic.gdx.utils.ObjectMap;
import org.worldloom.serialize.data.EntityData;
import org.worldloom.serialize.data.Properties;

public class GameSnapshot {
    public int saveFormatVersion = 1;
    public String curtMap;
    public ObjectMap<String, EntityData> entityData;
    public ObjectMap<String, Properties> systemProps;  // 改为使用类名作为key
    public GameSnapshot(){
        entityData = new ObjectMap<>();
        systemProps = new ObjectMap<>();
    }
}
