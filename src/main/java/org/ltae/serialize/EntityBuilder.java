package org.ltae.serialize;

import com.artemis.Aspect;
import com.artemis.AspectSubscriptionManager;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.manager.map.WorldStateManager;
import org.ltae.serialize.data.EntityData;
import org.ltae.serialize.data.EntityDatum;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @Auther WenLong
 * @Date 2025/5/13 10:14
 * @Description
 **/
public class EntityBuilder {
    private final static String TAG = EntityBuilder.class.getSimpleName();

    public static void buildEntitiesFromSave(World world) {
        String mapName = WorldStateManager.getInstance().getWorldState().curtMap;
        String json = WorldStateManager.getInstance().getEntityDataJson(mapName);
        if (json == null || json.isEmpty()) {
            Gdx.app.debug(TAG, "No saved entity data for map: " + mapName);
            return;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        world.getSystem(WorldSerializationManager.class).load(bais, SaveFileFormat.class);

        postLoadAll(world);
    }

    public static void buildEntitiesFromJson(World world, String json) {
        if (json == null || json.isEmpty()) {
            return;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        world.getSystem(WorldSerializationManager.class).load(bais, SaveFileFormat.class);

        postLoadAll(world);
    }

    public static void buildEntities(World world, String mapName) {
        String json = WorldStateManager.getInstance().getEntityDataJson(mapName);
        buildEntitiesFromJson(world, json);
    }

    public static void buildEntities(World world, EntityData entityData) {
        EntitySerializer.buildEntities(world, entityData);
    }

    public static int buildEntity(World world, EntityDatum entityDatum) {
        return EntitySerializer.buildEntity(world, entityDatum);
    }

    private static void postLoadAll(World world) {
        AspectSubscriptionManager asm = world.getSystem(AspectSubscriptionManager.class);
        ComponentMapper<SerializeComponent> mapper = world.getMapper(SerializeComponent.class);
        IntBag entities = asm.get(Aspect.all(SerializeComponent.class)).getEntities();

        for (int i = 0; i < entities.size(); i++) {
            int entityId = entities.get(i);
            SerializeComponent sc = mapper.get(entityId);

            sc.postLoad(world);

            List<Method> childMethods = new ArrayList<>();
            Class<?> clazz = sc.getClass();
            while (clazz != null && clazz != SerializeComponent.class) {
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(PostLoad.class)) {
                        childMethods.add(method);
                    }
                }
                clazz = clazz.getSuperclass();
            }
            Collections.reverse(childMethods);

            for (Method method : childMethods) {
                method.setAccessible(true);
                try {
                    method.invoke(sc, world);
                } catch (Exception e) {
                    Gdx.app.error(TAG, "Failed to invoke @PostLoad on " + sc.getClass().getSimpleName() + "#" + method.getName(), e);
                }
            }
        }
    }
}
