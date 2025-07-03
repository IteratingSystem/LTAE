package org.ltae.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import org.ltae.event.on.OnEntityEvent;
import org.ltae.tiled.ComponentLoader;
import org.ltae.tiled.TileParam;
import org.ltae.tiled.details.EntityDetails;
import org.ltae.tiled.details.SystemDetails;
import org.ltae.utils.ReflectionUtils;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:35
 * @Description 总线接收器,用于注册实体的事件接收器
 **/
public class OnEventComp extends Component implements ComponentLoader {
    private static final String TAG = OnEventComp.class.getSimpleName();
    @TileParam
    public String simpleName;

    @Override
    public void loader(SystemDetails systemDetails, EntityDetails entityDetails) {
        String onEventPkg = systemDetails.onEventPkg;
        String className = onEventPkg + "." + simpleName;
        if (className.isEmpty()){
            Gdx.app.error(TAG,"Failed to load OnEventComp,className is empty");
            return;
        }
        systemDetails.eventSystem.registerEvents(
                ReflectionUtils.createInstance(className, new Class[]{Entity.class}, new Entity[]{systemDetails.world.getEntity(entityDetails.entityId)}));
    }
}
