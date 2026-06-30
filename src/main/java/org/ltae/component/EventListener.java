package org.ltae.component;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import org.ltae.LtaePluginRule;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.event.listener.InteractListener;
import org.ltae.event.listener.OnlyInteractive;
import org.ltae.manager.ReflectionManager;
import org.ltae.serialize.data.EntityDatum;
import org.ltae.utils.ReflectionUtils;

import java.util.Set;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:35
 * @Description 交互组件
 **/
public class EventListener extends SerializeComponent implements Disposable {
    public boolean isOnlyInter;
    public Object onEvent;
    @Override
    public void reload(World world, EntityDatum entityDatum) {
        super.reload(world, entityDatum);
    }
    public void registerEvent(String simpleName){
        ReflectionManager reflectionManager = ReflectionManager.getInstance();
        Set<Class<? extends InteractListener>> subTypesOfWithGame = reflectionManager.getSubTypesOfWithGame(InteractListener.class);
        for (Class<? extends InteractListener> aClass : subTypesOfWithGame) {
            if (!aClass.getSimpleName().equals(simpleName)) {
                continue;
            }
            onEvent = reflectionManager.createObject(
                    aClass,
                    new Class[]{Entity.class},
                    new Entity[]{world.getEntity(entityId)}
            );
        }

        if (onEvent == null){
            Gdx.app.error(getTag(),"Failed to registerEvent,'onEvent' is null,SimpleName : " + simpleName);
            return;
        }
        eventSystem.registerEvents(onEvent);

        if (onEvent instanceof OnlyInteractive) {
            isOnlyInter = true;
        }
        Gdx.app.debug(getTag(),"Loaded registerEvent,SimpleName : " + simpleName);
    }

    @Override
    public void dispose() {
        if (onEvent instanceof InteractListener interactiveListener) {
            interactiveListener.entityId = -1;
        }
    }
}
