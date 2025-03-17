package org.engine;

import com.artemis.ArtemisPlugin;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.injection.FieldResolver;
import com.artemis.link.EntityLinkManager;
import com.artemis.managers.PlayerManager;
import com.artemis.managers.TagManager;
import com.artemis.managers.TeamManager;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.ExtendedComponentMapperPlugin;
import net.mostlyoriginal.plugin.ProfilerPlugin;
import org.engine.component.TileAnimations;
import org.engine.system.*;

/**
 * @Auther WenLong
 * @Date 2025/3/17 11:00
 * @Description 插件
 **/
public class EnginePlugin implements ArtemisPlugin {
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
        worldConfigurationBuilder.with(new AssetSystem(""));//资源系统
        worldConfigurationBuilder.with(new TiledMapManager());//地图管理系统
        worldConfigurationBuilder.with(new B2dSystem(1));//物理世界初始化
        //渲染前更新
        worldConfigurationBuilder.with(new PosFollowSystem(1)); //坐标跟随物理身体
        worldConfigurationBuilder.with(new StateSystem());//状态机系统
        worldConfigurationBuilder.with(new CameraSystem(1,1,1,1,true));//摄像机系统
        worldConfigurationBuilder.with(new TileAnimSystem());//动画系统
        //渲染
        worldConfigurationBuilder.with(new RenderTiledSystem(1));//渲染瓦片地图
        worldConfigurationBuilder.with(renderBatchingSystem);//渲染管线
        worldConfigurationBuilder.with(new RenderFrameSystem(renderBatchingSystem,1));      //渲染Region帧系统
        worldConfigurationBuilder.with(new RenderPhysicsSystem(true));//渲染物理效果系统(debug)
        //创建实体
        worldConfigurationBuilder.with(new TiledEntityFactory(1,""));
    }
}
