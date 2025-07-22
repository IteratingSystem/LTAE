package org.ltae.component;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import org.ltae.LtaePluginRule;
import org.ltae.manager.map.serialize.json.EntityData;
import org.ltae.utils.ReflectionUtils;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:35
 * @Description 交互组件
 **/
public class OnEvent extends SerializeComponent implements Disposable {

    public Object onEvent;
    @Override
    public void reload(World world, EntityData entityData) {
        super.reload(world, entityData);
    }
    public void registerEvent(String simpleName){
        String onEventPkg = LtaePluginRule.ON_EVENT_PKG;
        String className = onEventPkg + "." + simpleName;
        if (className.isEmpty()){
            Gdx.app.error(getTag(),"Failed to load OnEventComp,className is empty");
            return;
        }

        onEvent = ReflectionUtils.createInstance(className, new Class[]{Entity.class}, new Entity[]{world.getEntity(entityId)});
        eventSystem.registerEvents(onEvent);
    }

    @Override
    public void dispose() {
        onEvent = null;
    }
}
