package org.worldloom;

import com.artemis.BaseSystem;
import com.artemis.World;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.event.common.Event;
import org.worldloom.event.CameraEvent;
import org.worldloom.event.EntityEvent;
import org.worldloom.manager.map.GameSnapshotManager;

/** 持有一个 Artemis World 的 Worldloom 运行实例。 */
public final class WorldloomEngine implements AutoCloseable {
    private final World world;
    private final EventSystem events;
    private boolean disposed;

    WorldloomEngine(World world) {
        this.world = world;
        events = world.getSystem(EventSystem.class);
        events.dispatch(new EntityEvent(EntityEvent.BUILD_ALL));
    }

    public void update(float delta) {
        requireActive();
        world.setDelta(delta);
        world.process();
    }

    public void resize(int width, int height) {
        requireActive();
        CameraEvent event = new CameraEvent(CameraEvent.RESIZE);
        event.width = width;
        event.height = height;
        events.dispatch(event);
    }

    public void dispatch(Event event) {
        requireActive();
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null");
        }
        events.dispatch(event);
    }

    public <T extends BaseSystem> T getSystem(Class<T> systemType) {
        requireActive();
        return world.getSystem(systemType);
    }

    /** 兼容需要把 World 传给 ECS UI 的现有游戏代码。 */
    public World getWorld() {
        requireActive();
        return world;
    }

    public String createSaveJson() {
        requireActive();
        return GameSnapshotManager.getInstance().createSaveJson(world);
    }

    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public void close() {
        dispose();
    }

    public void dispose() {
        if (disposed) {
            return;
        }
        world.dispose();
        disposed = true;
    }

    private void requireActive() {
        if (disposed) {
            throw new IllegalStateException("WorldloomEngine is disposed");
        }
    }
}
