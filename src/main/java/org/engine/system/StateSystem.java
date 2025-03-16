package org.engine.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.engine.component.StateComp;

/**
 * @Auther WenLong
 * @Date 2025/2/14 16:45
 * @Description 状态系统
 **/
@All(StateComp.class)
public class StateSystem extends IteratingSystem {
    private M<StateComp> mStateComp;
    @Override
    protected void process(int entityId) {
        StateComp stateComp = mStateComp.get(entityId);
        stateComp.machine.update();
    }
}
