package org.ltae.manager.map;

import com.badlogic.gdx.utils.ObjectMap;

public class MapSave {
    public String curtMap;
    public ObjectMap<String, String> mapJsons;
    public MapSave(){
        mapJsons = new ObjectMap<>();
    }
}
