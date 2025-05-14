package org.ltae;

import com.artemis.ArtemisPlugin;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;
import com.artemis.managers.PlayerManager;
import com.artemis.managers.TagManager;
import com.artemis.managers.TeamManager;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.ExtendedComponentMapperPlugin;
import net.mostlyoriginal.plugin.ProfilerPlugin;
import org.ltae.system.*;

/**
 * @Auther WenLong
 * @Date 2025/3/17 11:00
 * @Description 插件
 **/
public class LtaePlugin implements ArtemisPlugin {
    private LtaePluginRule ltaePluginRule;

    //
    private CameraSystem cameraSystem;


    public LtaePlugin(){}
    @Override
    public void setup(WorldConfigurationBuilder worldConfigurationBuilder) {
        RenderBatchingSystem renderBatchingSystem = new RenderBatchingSystem();
        cameraSystem = new CameraSystem(
                LtaePluginRule.WIN_WIDTH,
                LtaePluginRule.WIN_HEIGHT,
                LtaePluginRule.CAMERA_ZOOM,
                LtaePluginRule.WORLD_SCALE);
        //官方插件
        worldConfigurationBuilder.dependsOn(ExtendedComponentMapperPlugin.class);//拓展组件映射
        worldConfigurationBuilder.dependsOn(ProfilerPlugin.class);//监控查询
        worldConfigurationBuilder.dependsOn(TagManager.class);//标签管理器
        worldConfigurationBuilder.dependsOn(PlayerManager.class);//玩家管理器
        worldConfigurationBuilder.dependsOn(TeamManager.class);//团队管理器
        worldConfigurationBuilder.dependsOn(EntityLinkManager.class);//实体连接管理器
        worldConfigurationBuilder.dependsOn(EventSystem.class);//事件总线
        //初始系统
        worldConfigurationBuilder.with(new AssetSystem(LtaePluginRule.SKIN_PATH));//资源系统
        worldConfigurationBuilder.with(new TiledMapManager(
                LtaePluginRule.MAP_NAME,
                LtaePluginRule.PHY_LAYER,
                LtaePluginRule.PREFABRICATED_MAP_NAME));//地图管理系统
        worldConfigurationBuilder.with(new B2dSystem(
                LtaePluginRule.G_X,
                LtaePluginRule.G_Y,
                LtaePluginRule.B2D_SLEEP,
                LtaePluginRule.WORLD_SCALE));//物理世界初始化
        //渲染前更新
        worldConfigurationBuilder.with(new PosFollowBodySystem(LtaePluginRule.WORLD_SCALE)); //坐标跟随物理身体
        worldConfigurationBuilder.with(new BTreeSystem());//行为树系统
        worldConfigurationBuilder.with(new StateSystem());//状态机系统
        worldConfigurationBuilder.with(cameraSystem);//摄像机系统
        worldConfigurationBuilder.with(new KeyframeShapeSystem());//动画帧形状系统
        worldConfigurationBuilder.with(new TileAnimSystem());//动画系统
        //渲染
        worldConfigurationBuilder.with(new RenderTiledSystem(LtaePluginRule.WORLD_SCALE));//渲染瓦片地图
        worldConfigurationBuilder.with(renderBatchingSystem);//渲染管线
        //渲染Region帧系统
        worldConfigurationBuilder.with(new RenderFrameSystem(
                renderBatchingSystem,
                LtaePluginRule.WORLD_SCALE));
        //渲染物理效果系统(debug)
        worldConfigurationBuilder.with(new RenderPhysicsSystem());
        //绘制UI
        worldConfigurationBuilder.with(new RenderUISystem(
                LtaePluginRule.UI_WIDTH,
                LtaePluginRule.UI_HEIGHT));
        //创建实体
        worldConfigurationBuilder.with(
                WorldConfigurationBuilder.Priority.LOWEST,
                new EntityFactory(
                        LtaePluginRule.COMPONENT_PKG,
                        LtaePluginRule.STATE_PKG,
                        LtaePluginRule.B2D_LISTENER_PKG,
                        LtaePluginRule.ENTITY_LAYER,
                        LtaePluginRule.WORLD_SCALE));
    }
}
