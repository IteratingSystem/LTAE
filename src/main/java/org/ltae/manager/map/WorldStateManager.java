package org.ltae.manager.map;

import com.artemis.BaseSystem;
import com.artemis.World;
import org.ltae.manager.JsonManager;
import org.ltae.serialize.EntitySerializer;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.SerializeSystem;
import org.ltae.serialize.data.EntityData;
import org.ltae.serialize.data.Properties;
import org.ltae.serialize.data.Property;
import org.ltae.system.TiledMapSystem;

import java.lang.reflect.Field;

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

        /* 保存系统参数 */
for (BaseSystem system : world.getSystems()) {
            Class<? extends BaseSystem> clazz = system.getClass();
            // 检查类是否有 @SerializeSystem 注解，而不是 instanceof
            if (!clazz.isAnnotationPresent(SerializeSystem.class)) {
                continue;          // 只关心我们自己标记的系统
            }

            Properties props = new Properties();          // 本系统所有可序列化字段

            /* 遍历 public 字段，只有带 @SerializeParam 的才处理 */
            for (Field f : clazz.getFields()) {
                if (!f.isAnnotationPresent(SerializeParam.class)) {
                    continue;
                }
                f.setAccessible(true);   // 保险，防止包权限问题
                Property p = new Property();
                p.key   = f.getName();
                p.type  = f.getType().getName();

                try {
                    // 真正的反射取值
                    p.value = f.get(system);
                } catch (IllegalAccessException e) {
                    // 理论上不会发生，因为我们提前 setAccessible(true)
                    throw new RuntimeException("Unable to access field: " + f, e);
                }
                props.add(p);
            }

            /* 写进 worldState */
            // 注意：worldState.systemProps 的 key 是 Class<BaseSystem>
            // 而 ss 的实际类型是 Class<? extends SerializeSystem>，可以安全强转

            worldState.systemProps.put(clazz.getName(), props);  // 保存类名而不是Class对象
        }
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
