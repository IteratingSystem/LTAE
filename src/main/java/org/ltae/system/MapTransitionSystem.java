package org.ltae.system;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import net.mostlyoriginal.api.event.common.EventSystem;
import org.ltae.component.B2dBody;
import org.ltae.component.Portal;
import org.ltae.component.Pos;
import org.ltae.event.CameraEvent;
import org.ltae.event.MapEvent;
import org.ltae.manager.map.WorldStateManager;
import org.ltae.serialize.EntityBuilder;
import org.ltae.serialize.EntityDeleter;
import org.ltae.serialize.EntitySerializer;
import org.ltae.serialize.data.EntityData;
import org.ltae.serialize.data.EntityDatum;

/**
 * Executes a map transition as one transaction: capture, move, switch, rebuild and relocate.
 */
public class MapTransitionSystem extends BaseSystem {
    private static final String TAG = MapTransitionSystem.class.getSimpleName();

    @Override
    protected void processSystem() {
    }

    public void teleport(Portal portal, int[] entityIds, int playerEntityId, boolean switchMap) {
        if (portal == null) {
            throw new IllegalArgumentException("portal cannot be null");
        }
        if (portal.targetMap == null || portal.targetMap.isBlank()) {
            throw new IllegalArgumentException("portal targetMap cannot be blank");
        }
        if (entityIds == null || entityIds.length == 0) {
            return;
        }

        String sourceMap = world.getSystem(TiledMapSystem.class).getCurrent();
        if (portal.targetMap.equals(sourceMap)) {
            relocateEntities(portal.targetPosEntity, entityIds);
            return;
        }

        WorldStateManager stateManager = WorldStateManager.getInstance();
        stateManager.captureCurrentWorld(world);

        EntityData sourceData = stateManager.getEntityData(sourceMap);
        EntityData targetData = stateManager.getEntityData(portal.targetMap);
        moveEntityData(sourceData, targetData, entityIds, switchMap);

        if (!switchMap) {
            return;
        }

        EntityDeleter.deleteAll(world, entityIds);
        changeRuntimeMap(portal.targetMap);
        EntityBuilder.buildEntities(world, targetData);
        relocateEntities(portal.targetPosEntity, entityIds);
        jumpCameraTo(playerEntityId);
        stateManager.changeCurrentMap(portal.targetMap);
    }

    private void moveEntityData(EntityData sourceData, EntityData targetData,
                                int[] entityIds, boolean switchMap) {
        for (int entityId : entityIds) {
            removeEntityDataByRuntimeId(sourceData, entityId);

            if (!switchMap) {
                EntityDatum entityDatum = EntitySerializer.createEntityDatum(world, entityId);
                targetData.add(entityDatum);
                EntityDeleter.deleteEntity(world, entityId);
            }
        }
    }

    private void removeEntityDataByRuntimeId(EntityData entityData, int entityId) {
        for (EntityDatum datum : entityData) {
            if (datum.entityId == entityId) {
                entityData.removeValue(datum, false);
                return;
            }
        }
    }

    private void changeRuntimeMap(String targetMap) {
        MapEvent mapEvent = new MapEvent(MapEvent.CHANGE_MAP);
        mapEvent.mapName = targetMap;
        world.getSystem(EventSystem.class).dispatch(mapEvent);
    }

    private void relocateEntities(String targetTag, int[] entityIds) {
        TagManager tagManager = world.getSystem(TagManager.class);
        ComponentMapper<Pos> positions = world.getMapper(Pos.class);
        ComponentMapper<B2dBody> bodies = world.getMapper(B2dBody.class);
        int targetId = targetTag == null ? -1 : tagManager.getEntityId(targetTag);

        if (targetId == -1 || !positions.has(targetId)) {
            Gdx.app.error(TAG, "Target position not found: " + targetTag);
            for (int entityId : entityIds) {
                setEntityPosition(positions, bodies, entityId, null);
            }
            return;
        }

        Pos targetPos = positions.get(targetId);
        for (int entityId : entityIds) {
            setEntityPosition(positions, bodies, entityId, targetPos);
        }
    }

    private void setEntityPosition(ComponentMapper<Pos> positions,
                                   ComponentMapper<B2dBody> bodies,
                                   int entityId, Pos targetPos) {
        if (!positions.has(entityId)) {
            Gdx.app.error(TAG, "Entity has no Pos component: " + entityId);
            return;
        }
        Pos pos = positions.get(entityId);
        if (targetPos == null) {
            pos.set(0, 0);
        } else {
            pos.copy(targetPos);
        }
        if (bodies.has(entityId)) {
            bodies.get(entityId).setPos(pos);
        }
    }

    private void jumpCameraTo(int entityId) {
        ComponentMapper<Pos> positions = world.getMapper(Pos.class);
        if (!positions.has(entityId)) {
            return;
        }
        CameraEvent cameraEvent = new CameraEvent(CameraEvent.JUMP_POS);
        cameraEvent.pos = positions.get(entityId);
        world.getSystem(EventSystem.class).dispatch(cameraEvent);
    }
}
