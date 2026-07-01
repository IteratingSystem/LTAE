package org.ltae.system;


import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import net.mostlyoriginal.api.event.common.Subscribe;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.ltae.component.inter.Interactive;
import org.ltae.event.InteractEvent;

/**
 * 处理实体被交互的系统
 *
 * <p></p>
 *
 * @author WenLong
 * @version 1.0.0
 * @date 2026/7/1 11:21
 * @see OnInteractSystem
 */
public class OnInteractSystem extends BaseSystem {
    private final static String TAG = OnInteractSystem.class.getSimpleName();
    private M<Interactive> mInteractive;

    @Override
    protected void processSystem() {

    }



    @Subscribe
    public void onEvent(InteractEvent interactEvent){
        int toId = interactEvent.toId;
        if (!mInteractive.has(toId)) {
            Gdx.app.error(TAG,"Failed to onEvent,Target entity is not has component 'Interactive';");
            return;
        }

        Interactive interactive = mInteractive.get(toId);
        interactive.onInteractListener.onEvent(interactEvent);
    }
}
