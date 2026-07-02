package org.ltae.component;

import com.artemis.ComponentMapper;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.event.CameraEvent;
import org.ltae.event.MapEvent;
import org.ltae.manager.map.WorldStateManager;
import org.ltae.serialize.EntityBuilder;
import org.ltae.serialize.EntityDeleter;
import org.ltae.serialize.EntitySerializer;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.data.EntityData;
import org.ltae.serialize.data.EntityDatum;

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


    /**
     * 将传入的id跳转到目标地点
     * switchMap: 是否要立即切换游戏地图,比如把玩家传送到另一幅图,则直接切换
     * @param entityIds
     * @param switchMap
     */
    public void teleport(int[] entityIds,int playerEntityId,boolean switchMap){
        TagManager tagManager = world.getSystem(TagManager.class);

        ComponentMapper<Pos> mPos = world.getMapper(Pos.class);
        ComponentMapper<B2dBody> mB2dBody = world.getMapper(B2dBody.class);

        WorldStateManager worldStateManager = WorldStateManager.getInstance();
        String curtMap = worldStateManager.getWorldState().curtMap;

        // 同地图跳转,只需要改pos
        if (targetMap.equals(curtMap)) {
            setPos(entityIds);
        }else {
            // 不同地图
            // 世界状态存档
            worldStateManager.updateWorldState(world);

            // 删除当前世界中,需要传送的实体j,并将其加入到目标世界的数据中
            EntityData cutData = worldStateManager.getEntityData(curtMap);
            EntityData targetData = worldStateManager.getEntityData(targetMap);
            for (int entityId : entityIds) {
                EntityDatum entityDatum = EntitySerializer.createEntityDatum(world, entityId);

                for (EntityDatum cutDatum : cutData) {
                    if (cutDatum.entityId != entityId) {
                        continue;
                    }

                    // 删除实体在当前世界的数据
                    cutData.removeValue(cutDatum, false);

                    // 当不需要跳转地图的时候,还需要删除跳转到目标的实体
                    if (!switchMap){
                        targetData.add(entityDatum);
                        EntityDeleter.deleteEntity(world, entityId);
                    }
                }
            }

            // 跳转地图
            if (!switchMap){
                return;
            }
            //删除所有实体,(保留需要跳转的实体)
            EntityDeleter.deleteAll(world, entityIds);

            // 切换至目标地图
            MapEvent mapEvent = new MapEvent(MapEvent.CHANGE_MAP);
            mapEvent.mapName = targetMap;
            eventSystem.dispatch(mapEvent);
            // 创建目标地图的实体
            EntityBuilder.buildEntities(world,targetData);
            // 将当前实体跳转到目标坐标
            setPos(entityIds);

            Pos playerPos = mPos.get(playerEntityId);
            // 直接切换摄像头坐标
            CameraEvent cameraEvent = new CameraEvent(CameraEvent.JUMP_POS);
            cameraEvent.pos = playerPos;
            eventSystem.dispatch(cameraEvent);

            // 切换当前地图
            worldStateManager.getWorldState().curtMap = targetMap;
        }
    }

    private void setPos(int[] entityIds){
        TagManager tagManager = world.getSystem(TagManager.class);
        ComponentMapper<Pos> mPos = world.getMapper(Pos.class);
        ComponentMapper<B2dBody> mB2dBody = world.getMapper(B2dBody.class);
        int targetId = tagManager.getEntityId(targetPosEntity);

        // 没有找到目标跳转到0
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
        // 有目标跳转
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
