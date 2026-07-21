package org.ltae.component.input;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Disposable;
import net.mostlyoriginal.api.event.common.EventSystem;
import org.ltae.manager.input.InputManager;
import org.ltae.manager.input.LongPressListener;

/**
 * 处理输入
 */

public abstract class InputProcessing implements LongPressListener, Disposable {
    public boolean isInited = false;

    public World world;
    public TagManager tagManager;
    public EventSystem eventSystem;
    public Entity entity;

    public InputProcessing() {
    }

    public void init(Entity entity){
        isInited = true;
        this.entity = entity;
        world = entity.getWorld();
        tagManager = world.getSystem(TagManager.class);
        eventSystem = world.getSystem(EventSystem.class);
    }
    public abstract void update();

    @Override
    public void dispose() {
        InputManager.removeLongPressListener(this);
    }

    public String getTag(){
        return getClass().getSimpleName();
    }
}
