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
public class EnginePlugin implements ArtemisPlugin {
    // 全局属性
    private float windowWidth;      // 窗口宽度
    private float windowHeight;     // 窗口高度
    private float cameraZoom;             // 缩放比例
    private float worldScale;       // 世界缩放比例
    private String tileMapPath;     // 瓦片地图路径
    private String compPackagePath; // 组件包路径

    public EnginePlugin(float windowWidth,float windowHeight,
                        float cameraZoom,float worldScale,
                        String tileMapPath,
                        String compPackagePath){
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.cameraZoom = cameraZoom;
        this.worldScale = worldScale;
        this.tileMapPath = tileMapPath;
        this.compPackagePath = compPackagePath;
    }
    @Override
    public void setup(WorldConfigurationBuilder worldConfigurationBuilder) {
        RenderBatchingSystem renderBatchingSystem = new RenderBatchingSystem();
        //官方插件
        worldConfigurationBuilder.dependsOn(ExtendedComponentMapperPlugin.class);//拓展组件映射
        worldConfigurationBuilder.dependsOn(ProfilerPlugin.class);//监控查询
        worldConfigurationBuilder.dependsOn(TagManager.class);//标签管理器
        worldConfigurationBuilder.dependsOn(PlayerManager.class);//玩家管理器
        worldConfigurationBuilder.dependsOn(TeamManager.class);//团队管理器
        worldConfigurationBuilder.dependsOn(EntityLinkManager.class);//实体连接管理器
        worldConfigurationBuilder.dependsOn(EventSystem.class);//事件总线
        //初始系统
        worldConfigurationBuilder.with(new AssetSystem(tileMapPath));//资源系统
        worldConfigurationBuilder.with(new TiledMapManager());//地图管理系统
        worldConfigurationBuilder.with(new B2dSystem(worldScale));//物理世界初始化
        //渲染前更新
        worldConfigurationBuilder.with(new PosFollowSystem(worldScale)); //坐标跟随物理身体
        worldConfigurationBuilder.with(new StateSystem());//状态机系统
        worldConfigurationBuilder.with(new CameraSystem(windowWidth,windowHeight,cameraZoom,worldScale));//摄像机系统
        worldConfigurationBuilder.with(new TileAnimSystem());//动画系统
        //渲染
        worldConfigurationBuilder.with(new RenderTiledSystem(worldScale));//渲染瓦片地图
        worldConfigurationBuilder.with(renderBatchingSystem);//渲染管线
        worldConfigurationBuilder.with(new RenderFrameSystem(renderBatchingSystem,worldScale));      //渲染Region帧系统
        worldConfigurationBuilder.with(new RenderPhysicsSystem());//渲染物理效果系统(debug)
        //创建实体
        worldConfigurationBuilder.with(new TiledEntityFactory(windowHeight,compPackagePath));
    }
}
