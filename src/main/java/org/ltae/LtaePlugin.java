package org.ltae;

import com.artemis.ArtemisPlugin;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;
import com.artemis.managers.PlayerManager;
import com.artemis.managers.TagManager;
import com.artemis.managers.TeamManager;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.event.common.SubscribeAnnotationFinder;
import net.mostlyoriginal.api.event.dispatcher.FastEventDispatcher;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.ExtendedComponentMapperPlugin;
import net.mostlyoriginal.plugin.ProfilerPlugin;
import org.ltae.system.*;

/**
 * @Auther WenLong
 * @Date 2025/3/17 11:00
 * @Description 插件
 **/
public class LtaePlugin implements ArtemisPlugin {
    private CameraSystem cameraSystem;


    public LtaePlugin(){}
    @Override
    public void setup(WorldConfigurationBuilder worldConfigurationBuilder) {
        RenderBatchingSystem renderBatchingSystem = new RenderBatchingSystem();
        cameraSystem = new CameraSystem(
                LtaePluginRule.GAME_WIDTH,
                LtaePluginRule.GAME_HEIGHT,
                LtaePluginRule.CAMERA_ZOOM,
                LtaePluginRule.WORLD_SCALE);
        SubscribeAnnotationFinder subscribeAnnotationFinder = new SubscribeAnnotationFinder();
        FastEventDispatcher fastEventDispatcher = new FastEventDispatcher();
        EventSystem eventSystem = new EventSystem(fastEventDispatcher,subscribeAnnotationFinder);
        //官方插件
        worldConfigurationBuilder.dependsOn(ExtendedComponentMapperPlugin.class);//拓展组件映射
        worldConfigurationBuilder.dependsOn(ProfilerPlugin.class);//监控查询
        worldConfigurationBuilder.dependsOn(TagManager.class);//标签管理器
        worldConfigurationBuilder.dependsOn(PlayerManager.class);//玩家管理器
        worldConfigurationBuilder.dependsOn(TeamManager.class);//团队管理器
        worldConfigurationBuilder.dependsOn(EntityLinkManager.class);//实体连接管理器
        worldConfigurationBuilder.with(eventSystem);//事件总线
        //初始系统
        worldConfigurationBuilder.with(new AssetSystem(LtaePluginRule.SKIN_PATH));//资源系统
        worldConfigurationBuilder.with(new TiledMapSystem(
                LtaePluginRule.MAP_NAME,
                LtaePluginRule.ENTITY_LAYERS,
                LtaePluginRule.PHY_LAYERS));
        worldConfigurationBuilder.with(new B2dSystem(
                LtaePluginRule.G_X,
                LtaePluginRule.G_Y,
                LtaePluginRule.B2D_SLEEP,
                LtaePluginRule.WORLD_SCALE,
                LtaePluginRule.COMB_TILE));//物理世界初始化
        //渲染前更新
        worldConfigurationBuilder.with(new PosFollowBodySystem(LtaePluginRule.WORLD_SCALE)); //坐标跟随物理身体
        worldConfigurationBuilder.with(new BTreeSystem());//行为树系统
        worldConfigurationBuilder.with(new StateSystem());//状态机系统
        worldConfigurationBuilder.with(cameraSystem);//摄像机系统
        worldConfigurationBuilder.with(new KeyframeShapeSystem());//动画帧形状系统
        worldConfigurationBuilder.with(new TileAnimSystem());//动画系统
        worldConfigurationBuilder.with(new LayerSamplingSystem());//图层采样
        worldConfigurationBuilder.with(new ZIndexSystem());//渲染顺序,越小越提前渲染,也就越滞后
        //渲染
        worldConfigurationBuilder.with(new RenderTiledSystem(LtaePluginRule.WORLD_SCALE));//渲染瓦片地图
        worldConfigurationBuilder.with(renderBatchingSystem);//渲染管线
        //渲染Region帧系统
        worldConfigurationBuilder.with(new RenderFrameSystem(
                renderBatchingSystem,
                LtaePluginRule.WORLD_SCALE));
        //渲染物理效果系统(debug)
        worldConfigurationBuilder.with(new RenderPhysicsSystem());

        //自动还原系统属性
        worldConfigurationBuilder.with(
                WorldConfigurationBuilder.Priority.LOWEST,
                new SysPropsRestoreSystem(true)
        );
        //创建实体
        worldConfigurationBuilder.with(
                WorldConfigurationBuilder.Priority.LOWEST,
                new EntityFactory());
        //创建实体
        worldConfigurationBuilder.with(
                WorldConfigurationBuilder.Priority.LOWEST,
                new LightSystem(LtaePluginRule.ENABLE_LIGHT));
        //绘制UI,放在最后
        worldConfigurationBuilder.with(
                WorldConfigurationBuilder.Priority.LOWEST,
                new RenderUISystem(
                LtaePluginRule.UI_WIDTH,
                LtaePluginRule.UI_HEIGHT));
    }
}
