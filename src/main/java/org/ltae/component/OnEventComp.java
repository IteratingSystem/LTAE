package org.ltae.component;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import net.mostlyoriginal.api.event.common.EventSystem;
import org.ltae.LtaePluginRule;
import org.ltae.serialize.SerializeParam;
import org.ltae.utils.ReflectionUtils;
import org.ltae.serialize.json.EntityJson;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:35
 * @Description 总线接收器,用于注册实体的事件接收器
 **/
public class OnEventComp extends SerializeComponent{
    private static final String TAG = OnEventComp.class.getSimpleName();
    @SerializeParam
    public String simpleName;

    @Override
    public void reload(World world, EntityJson entityJson) {
        super.reload(world,entityJson);
        String onEventPkg = LtaePluginRule.ON_EVENT_PKG;
        String className = onEventPkg + "." + simpleName;
        if (className.isEmpty()){
            Gdx.app.error(TAG,"Failed to load OnEventComp,className is empty");
            return;
        }
        EventSystem eventSystem = world.getSystem(EventSystem.class);
        eventSystem.registerEvents(
                ReflectionUtils.createInstance(className, new Class[]{Entity.class}, new Entity[]{world.getEntity(entityId)}));
    }
}
