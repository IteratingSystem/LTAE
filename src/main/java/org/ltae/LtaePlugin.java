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
import org.ltae.environment.CloudShadowConfig;
import org.ltae.environment.WindConfig;
import org.ltae.light.AmbientLightConfig;
import org.ltae.light.AmbientLightTimeSource;
import org.ltae.light.SunLightConfig;
import org.ltae.light.TopDownShadowConfig;
import org.ltae.manager.map.GameSnapshotManager;
import org.ltae.shader.TileLayerShaderConfig;
import org.ltae.system.*;

import com.badlogic.gdx.utils.Array;

/**
 * @Auther WenLong
 * @Date 2025/3/17 11:00
 * @Description 插件
 **/
public class LtaePlugin implements ArtemisPlugin {
    private CameraSystem cameraSystem;
    private AmbientLightTimeSource lightTimeSource;
    private AmbientLightConfig ambientLightConfig;
    private TopDownShadowConfig topDownShadowConfig;
    private SunLightConfig sunLightConfig;
    private WindConfig windConfig;
    private CloudShadowConfig cloudShadowConfig;
    private final Array<TileLayerShaderConfig> tileLayerShaderConfigs =
        new Array<>();


    public LtaePlugin(){}

    /**
     * 配置由引擎统一排序和注册的动态环境光与俯视角光影系统。
     */
    public LtaePlugin configureLighting(
        AmbientLightTimeSource lightTimeSource,
        AmbientLightConfig ambientLightConfig,
        TopDownShadowConfig topDownShadowConfig) {
        return configureLighting(lightTimeSource, ambientLightConfig,
            topDownShadowConfig, new SunLightConfig());
    }

    public LtaePlugin configureLighting(
        AmbientLightTimeSource lightTimeSource,
        AmbientLightConfig ambientLightConfig,
        TopDownShadowConfig topDownShadowConfig,
        SunLightConfig sunLightConfig) {
        if (lightTimeSource == null || ambientLightConfig == null
            || topDownShadowConfig == null || sunLightConfig == null) {
            throw new IllegalArgumentException(
                "lighting time source and configs cannot be null");
        }
        this.lightTimeSource = lightTimeSource;
        this.ambientLightConfig = ambientLightConfig;
        this.topDownShadowConfig = topDownShadowConfig;
        this.sunLightConfig = sunLightConfig;
        return this;
    }

    /**
     * 注册由引擎统一排序的Shader瓦片层。
     */
    public LtaePlugin addShaderTileLayer(TileLayerShaderConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }
        tileLayerShaderConfigs.add(config);
        return this;
    }

    /**
     * 配置由海流、云影等环境效果共享的风，以及云影外观。
     */
    public LtaePlugin configureWind(WindConfig windConfig,
                                    CloudShadowConfig cloudShadowConfig) {
        if (windConfig == null || cloudShadowConfig == null) {
            throw new IllegalArgumentException(
                "wind and cloud shadow configs cannot be null");
        }
        this.windConfig = windConfig;
        this.cloudShadowConfig = cloudShadowConfig;
        return this;
    }

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
        String curtMap = GameSnapshotManager.getInstance().getCurrentMap();
        worldConfigurationBuilder.with(new TiledMapSystem(
                curtMap != null && !curtMap.isBlank() ? curtMap : LtaePluginRule.MAP_NAME,
                LtaePluginRule.ENTITY_LAYERS,
                LtaePluginRule.PHY_LAYERS));
        worldConfigurationBuilder.with(new B2dSystem(
                LtaePluginRule.G_X,
                LtaePluginRule.G_Y,
                LtaePluginRule.B2D_SLEEP,
                LtaePluginRule.WORLD_SCALE,
                LtaePluginRule.COMB_TILE));//物理世界初始化
        if (windConfig != null) {
            worldConfigurationBuilder.with(new WindSystem(windConfig));
        }
        //渲染前更新
        worldConfigurationBuilder.with(new InputProcessSystem()); // 输入处理
        worldConfigurationBuilder.with(new OnInteractSystem()); // 实体被交互处理系统
        worldConfigurationBuilder.with(new MapTransitionSystem()); // 地图切换事务
        worldConfigurationBuilder.with(new PosFollowBodySystem(LtaePluginRule.WORLD_SCALE)); //坐标跟随物理身体
        worldConfigurationBuilder.with(new BTreeSystem());//行为树系统
        worldConfigurationBuilder.with(new StateSystem());//状态机系统
        worldConfigurationBuilder.with(cameraSystem);//摄像机系统
        worldConfigurationBuilder.with(new KeyframeShapeSystem());//动画帧形状系统
        worldConfigurationBuilder.with(new TileAnimSystem());//动画系统
        worldConfigurationBuilder.with(new LayerSamplingSystem());//图层采样
        worldConfigurationBuilder.with(new ZIndexSystem());//渲染顺序
        if (lightTimeSource != null) {
            worldConfigurationBuilder.with(new DynamicAmbientLight(
                lightTimeSource, ambientLightConfig));
            worldConfigurationBuilder.with(new DynamicSunLight(
                lightTimeSource, sunLightConfig));
        }

        //渲染
        worldConfigurationBuilder.with(new RenderTiledSystem(LtaePluginRule.WORLD_SCALE));//渲染瓦片地图
        for (TileLayerShaderConfig config : tileLayerShaderConfigs) {
            worldConfigurationBuilder.with(
                new ShaderTileLayerRenderSystem(config));
        }
        worldConfigurationBuilder.with(renderBatchingSystem);//渲染管线
        //渲染Region帧系统
        worldConfigurationBuilder.with(new RenderFrameSystem(
                renderBatchingSystem,
                LtaePluginRule.WORLD_SCALE));
        //渲染物理效果系统(debug)
        worldConfigurationBuilder.with(new RenderPhysicsSystem());

        if (lightTimeSource != null) {
            worldConfigurationBuilder.with(
                WorldConfigurationBuilder.Priority.LOWEST,
                new TopDownShadowSystem(
                    LtaePluginRule.WORLD_SCALE, topDownShadowConfig,
                    sunLightConfig));
        }

        //自动还原系统属性
        worldConfigurationBuilder.with(
                WorldConfigurationBuilder.Priority.LOWEST,
                new SysRestoreSystem(true)
        );
        //创建实体
        worldConfigurationBuilder.with(
                WorldConfigurationBuilder.Priority.LOWEST,
                new EntityFactory());
        //光源系统
        worldConfigurationBuilder.with(
                WorldConfigurationBuilder.Priority.LOWEST,
                new LightSystem(LtaePluginRule.ENABLE_LIGHT));
        if (windConfig != null) {
            worldConfigurationBuilder.with(
                WorldConfigurationBuilder.Priority.LOWEST,
                new CloudShadowSystem(cloudShadowConfig));
        }
        if (lightTimeSource != null) {
            worldConfigurationBuilder.with(
                WorldConfigurationBuilder.Priority.LOWEST,
                new TopDownPointLightRenderSystem());
        }
        //绘制UI,放在最后
        worldConfigurationBuilder.with(
                WorldConfigurationBuilder.Priority.LOWEST,
                new RenderUISystem(
                LtaePluginRule.UI_WIDTH,
                LtaePluginRule.UI_HEIGHT));
    }
}
