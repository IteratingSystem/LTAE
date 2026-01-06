package org.ltae.manager.map;

import com.artemis.World;
import org.ltae.manager.JsonManager;
import org.ltae.serialize.EntitySerializer;
import org.ltae.serialize.data.EntityData;
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

        EntityData entityData = EntitySerializer.createEntityData(world);
        worldState.entityData.put(curtMap, entityData);
    }
    public EntityData getEntityData(String mapName){
        EntityData entityData = worldState.entityData.get(mapName);
        if (entityData == null) {
            entityData = new EntityData();
        }
        return entityData;
    }
    public String serializeState(){
        return JsonManager.toJson(worldState);
    }
}
