package org.ltae.manager.map;

import com.artemis.World;
import org.ltae.manager.JsonManager;
import org.ltae.manager.map.serialize.EntitySerializer;
import org.ltae.manager.map.serialize.json.EntityDataList;
import org.ltae.system.TiledMapSystem;

public class WorldStateManager {
    private static WorldStateManager instance;
    private WorldState worldState;
    private WorldStateManager(){

    }

    public static WorldStateManager getInstance(){
        if (instance == null) {
            instance = new WorldStateManager();
        }
        return instance;
    }
    public void setWorldState(WorldState worldState){
        this.worldState = worldState;
    }
    public WorldState getWorldState(){
        return worldState;
    }
    public void updateState(World world){
        TiledMapSystem tiledMapSystem = world.getSystem(TiledMapSystem.class);
        String curtMap = tiledMapSystem.getCurrent();
        worldState.curtMap = curtMap;

        EntityDataList entityDataList = EntitySerializer.getEntityDataList(world);
        worldState.entityData.put(curtMap,entityDataList);
    }
    public EntityDataList getEntityDataList(String mapName){
        return worldState.entityData.get(mapName);
    }
    public String serializeState(){
        return JsonManager.toJson(worldState);
    }
}
