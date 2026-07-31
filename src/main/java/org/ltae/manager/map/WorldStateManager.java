package org.ltae.manager.map;

import com.artemis.Aspect;
import com.artemis.AspectSubscriptionManager;
import com.artemis.BaseSystem;
import com.artemis.World;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import org.ltae.manager.JsonManager;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.SerializeSystem;
import org.ltae.serialize.data.Properties;
import org.ltae.serialize.data.Property;
import org.ltae.system.TiledMapSystem;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

public class WorldStateManager {
    private final static String TAG = WorldStateManager.class.getSimpleName();
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
    public static void setNull(){
        instance = null;
    }
    public void setWorldState(WorldState worldState){
        this.worldState = worldState;
    }
    public WorldState getWorldState(){
        return worldState;
    }

    public void updateWorldState(World world){
        TiledMapSystem tiledMapSystem = world.getSystem(TiledMapSystem.class);
        String curtMap = tiledMapSystem.getCurrent();
        worldState.curtMap = curtMap;

        AspectSubscriptionManager asm = world.getSystem(AspectSubscriptionManager.class);
        IntBag allEntities = asm.get(Aspect.all()).getEntities();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        world.getSystem(WorldSerializationManager.class).save(baos, new SaveFileFormat(allEntities));
        worldState.entityDataJson.put(curtMap, baos.toString(StandardCharsets.UTF_8));

        for (BaseSystem system : world.getSystems()) {
            Class<? extends BaseSystem> clazz = system.getClass();
            if (!clazz.isAnnotationPresent(SerializeSystem.class)) {
                continue;
            }

            Properties props = new Properties();
            for (Field f : clazz.getFields()) {
                if (!f.isAnnotationPresent(SerializeParam.class)) {
                    continue;
                }
                f.setAccessible(true);
                Property p = new Property();
                p.key   = f.getName();
                p.type  = f.getType().getName();
                try {
                    p.value = f.get(system);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Unable to access field: " + f, e);
                }
                props.add(p);
            }
            worldState.systemProps.put(clazz.getName(), props);
        }
    }

    public String getEntityDataJson(String mapName){
        return worldState.entityDataJson.get(mapName);
    }

    public void setEntityDataJson(String mapName, String json){
        worldState.entityDataJson.put(mapName, json);
    }

    public String getSaveJson(){
        return JsonManager.toJson(worldState);
    }
}
