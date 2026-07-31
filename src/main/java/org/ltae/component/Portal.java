package org.ltae.component;

import com.artemis.ComponentMapper;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.TagManager;
import com.artemis.managers.WorldSerializationManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.event.CameraEvent;
import org.ltae.event.MapEvent;
import org.ltae.manager.map.WorldStateManager;
import org.ltae.serialize.EntityBuilder;
import org.ltae.serialize.EntityDeleter;
import org.ltae.serialize.SerializeParam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @Auther WenLong
 * @Date 2025/7/15 10:31
 * @Description 传送门,记录跳转到目标地图的组件
 **/
public class Portal extends SerializeComponent {
    private final static String TAG = Portal.class.getSimpleName();

    // 目标地图
    @SerializeParam
    public String targetMap;
    // 跳转新地图后,一般记录玩家初始坐标的实体(tag)
    @SerializeParam
    public String targetPosEntity;


    public void teleport(int[] entityIds, int playerEntityId, boolean switchMap){
        TagManager tagManager = world.getSystem(TagManager.class);

        ComponentMapper<Pos> mPos = world.getMapper(Pos.class);
        ComponentMapper<B2dBody> mB2dBody = world.getMapper(B2dBody.class);

        WorldStateManager worldStateManager = WorldStateManager.getInstance();
        String curtMap = worldStateManager.getWorldState().curtMap;

        if (targetMap.equals(curtMap)) {
            setPos(entityIds);
        } else {
            // 世界状态存档
            worldStateManager.updateWorldState(world);

            // 获取当前地图和目标地图的 Artemis JSON
            String curtJson = worldStateManager.getEntityDataJson(curtMap);
            String targetJson = worldStateManager.getEntityDataJson(targetMap);

            Json json = new Json();
            json.setUsePrototypes(false);

            JsonValue curtRoot = new JsonReader().parse(curtJson);
            JsonValue curtEntities = curtRoot.get("entities");

            JsonValue targetRoot = (targetJson != null && !targetJson.isEmpty())
                ? new JsonReader().parse(targetJson)
                : createEmptyArtemisSave();
            JsonValue targetEntities = targetRoot.get("entities");

            for (int entityId : entityIds) {
                String idStr = Integer.toString(entityId);
                JsonValue entityValue = curtEntities.get(idStr);
                if (entityValue != null) {
                    // 从当前地图移除
                    removeChildByName(curtEntities, idStr);
                    if (!switchMap) {
                        // 添加到目标地图
                        targetEntities.addChild(idStr, entityValue);
                        EntityDeleter.deleteEntity(world, entityId);
                    }
                }
            }

            // 写回 JSON
            Json jsonWrite = new Json(com.badlogic.gdx.utils.JsonWriter.OutputType.json);
            worldStateManager.setEntityDataJson(curtMap, jsonWrite.toJson(curtRoot));
            worldStateManager.setEntityDataJson(targetMap, jsonWrite.toJson(targetRoot));

            if (!switchMap) {
                return;
            }
            // 删除所有实体(保留需要跳转的)
            EntityDeleter.deleteAll(world, entityIds);

            // 切换至目标地图
            MapEvent mapEvent = new MapEvent(MapEvent.CHANGE_MAP);
            mapEvent.mapName = targetMap;
            eventSystem.dispatch(mapEvent);

            // 创建目标地图的实体
            EntityBuilder.buildEntitiesFromJson(world, targetJson);
            setPos(entityIds);

            Pos playerPos = mPos.get(playerEntityId);
            CameraEvent cameraEvent = new CameraEvent(CameraEvent.JUMP_POS);
            cameraEvent.pos = playerPos;
            eventSystem.dispatch(cameraEvent);

            worldStateManager.getWorldState().curtMap = targetMap;
        }
    }

    private void removeChildByName(JsonValue parent, String name) {
        for (JsonValue child = parent.child; child != null; child = child.next) {
            if (name.equals(child.name())) {
                parent.remove(name);
                return;
            }
        }
    }

    private JsonValue createEmptyArtemisSave() {
        Json json = new Json();
        SaveFileFormat empty = new SaveFileFormat();
        String emptyJson = json.toJson(empty);
        return new JsonReader().parse(emptyJson);
    }

    private void setPos(int[] entityIds){
        TagManager tagManager = world.getSystem(TagManager.class);
        ComponentMapper<Pos> mPos = world.getMapper(Pos.class);
        ComponentMapper<B2dBody> mB2dBody = world.getMapper(B2dBody.class);
        int targetId = tagManager.getEntityId(targetPosEntity);

        if (targetId == -1) {
            Gdx.app.error(TAG,"Target pos not found!");
            for (int id : entityIds) {
                Pos pos = mPos.get(id);
                pos.set(0,0);
                if (mB2dBody.has(id)) {
                    B2dBody b2dBody = mB2dBody.get(id);
                    b2dBody.setPos(pos);
                }
            }
            return;
        }
        Pos targetPos = mPos.get(targetId);
        for (int id : entityIds) {
            Pos pos = mPos.get(id);
            pos.copy(targetPos);
            if (mB2dBody.has(id)) {
                B2dBody b2dBody = mB2dBody.get(id);
                b2dBody.setPos(pos);
            }
        }
    }
}
