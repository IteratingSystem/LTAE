package org.ltae.serialize;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.TagManager;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.manager.map.MapManager;
import org.ltae.serialize.data.EntityData;
import org.ltae.serialize.data.EntityDatum;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * 从 Tiled MapObjects 或 EntityData 生成 Artemis WorldSerializationManager 兼容的 JSON。
 *
 * @author WenLong
 * @version 1.0.0
 * @date 2026/7/28
 */
public class ArtemisTemplateGenerator {

    private static final String TAG = ArtemisTemplateGenerator.class.getSimpleName();

    public static String generateForMap(String mapName) {
        MapManager mapManager = MapManager.getInstance();
        MapObjects mapObjects = mapManager.getMapObjects(mapName);
        if (mapObjects == null) {
            Gdx.app.error(TAG, "No MapObjects found for map: " + mapName);
            return null;
        }

        EntityData entityData = EntitySerializer.createEntityData(mapName, mapObjects);
        return generateFromEntityData(entityData);
    }

    public static String generateFromEntityData(EntityData entityData) {
        World tempWorld = createTempWorld();
        WorldSerializationManager serializationManager = tempWorld.getSystem(WorldSerializationManager.class);

        IntBag entityIds = new IntBag();
        for (EntityDatum datum : entityData) {
            int entityId = EntitySerializer.buildEntityFieldsOnly(tempWorld, datum);
            entityIds.add(entityId);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serializationManager.save(baos, new SaveFileFormat(entityIds));

        tempWorld.dispose();

        return baos.toString(StandardCharsets.UTF_8);
    }

    public static ObjectMap<String, String> generateAll() {
        ObjectMap<String, String> templates = new ObjectMap<>();
        MapManager mapManager = MapManager.getInstance();

        for (ObjectMap.Entry<String, EntityData> entry : mapManager.getProtoEntityDate()) {
            String json = generateForMap(entry.key);
            if (json != null) {
                templates.put(entry.key, json);
            }
        }
        return templates;
    }

    public static void generateAndSave(String outputDir) {
        ObjectMap<String, String> templates = generateAll();
        for (ObjectMap.Entry<String, String> entry : templates) {
            String path = outputDir + entry.key + ".json";
            Gdx.files.local(path).writeString(entry.value, false);
            Gdx.app.log(TAG, "Saved template: " + path);
        }
    }

    private static World createTempWorld() {
        WorldConfigurationBuilder configBuilder = new WorldConfigurationBuilder();
        configBuilder.with(new WorldSerializationManager());
        configBuilder.with(new TagManager());

        World tempWorld = new World(configBuilder.build());
        tempWorld.getSystem(WorldSerializationManager.class).setSerializer(new JsonArtemisSerializer(tempWorld));

        return tempWorld;
    }
}
