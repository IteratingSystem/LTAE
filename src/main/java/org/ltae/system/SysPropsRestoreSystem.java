package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import net.mostlyoriginal.api.event.common.Subscribe;
import org.ltae.event.SystemEvent;
import org.ltae.manager.map.WorldState;
import org.ltae.manager.map.WorldStateManager;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.SerializeSystem;
import org.ltae.serialize.data.Properties;
import org.ltae.serialize.data.Property;

import java.lang.reflect.Field;

/**
 * @author: SysPropsSestoreSystem
 * @date: 2026/1/13
 * @description: 系统属性还原系统
 */

public class SysPropsRestoreSystem extends BaseSystem {
    private final static String TAG = SysPropsRestoreSystem.class.getSimpleName();

    //自动还原
    private boolean autoRestore;

    public SysPropsRestoreSystem(boolean autoRestore){
        this.autoRestore = autoRestore;
    }

    @Override
    protected void initialize() {
        super.initialize();

        if (!autoRestore){
            return;
        }
        restoreProps();
    }

    @Override
    protected void processSystem() {

    }


    @Subscribe
    public void onEvent(SystemEvent systemEvent){
        if (systemEvent.type == SystemEvent.RESTORE_PROPS) {
            restoreProps();
        }
    }

    private void restoreProps() {
        WorldState worldState = WorldStateManager.getInstance().getWorldState();
        ObjectMap<Class<BaseSystem>, Properties> systemProps = worldState.systemProps;
        if (systemProps == null) {
            Gdx.app.debug(TAG, "Failed to restoreProps, 'systemProps' is null!");
            return;
        }

        for (ObjectMap.Entry<Class<BaseSystem>, Properties> entry : systemProps) {
            Class<BaseSystem> key = entry.key;
            Properties props = entry.value;

            /* 找到对应的系统实例 */
            BaseSystem target = world.getSystem(key);
            if (target == null) {
                Gdx.app.debug(TAG, "System " + key.getSimpleName() + " not found in world, skip.");
                continue;
            }
            /* 只处理我们自己标记的 SerializeSystem */
            if (!(target instanceof SerializeSystem)) {
                continue;
            }

            /* 逐字段还原 */
            for (Property p : props) {
                try {
                    Field f = target.getClass().getField(p.key);   // 字段必须是 public
                    if (!f.isAnnotationPresent(SerializeParam.class)) {
                        continue;   // 防御：理论上不会发生
                    }
                    f.setAccessible(true);
                    f.set(target, p.value);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    Gdx.app.error(TAG, "Failed to restore field: " + p.key + " in " + key.getSimpleName(), e);
                }
            }
        }
    }
}
