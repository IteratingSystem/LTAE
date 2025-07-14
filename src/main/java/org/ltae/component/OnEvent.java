package org.ltae.component;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import net.mostlyoriginal.api.event.common.EventSystem;
import org.ltae.LtaePluginRule;
import org.ltae.manager.map.serialize.SerializeParam;
import org.ltae.manager.map.serialize.json.EntityData;
import org.ltae.utils.ReflectionUtils;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:35
 * @Description 交互组件
 **/
public class OnEvent extends SerializeComponent{
    @SerializeParam
    public String simpleName;

    @Override
    public void reload(World world, EntityData entityData) {
        super.reload(world, entityData);
        String onEventPkg = LtaePluginRule.ON_EVENT_PKG;
        String className = onEventPkg + "." + simpleName;
        if (className.isEmpty()){
            Gdx.app.error(getTag(),"Failed to load OnEventComp,className is empty");
            return;
        }
        EventSystem eventSystem = world.getSystem(EventSystem.class);
        eventSystem.registerEvents(
                ReflectionUtils.createInstance(className, new Class[]{Entity.class}, new Entity[]{world.getEntity(entityId)}));
    }
}
