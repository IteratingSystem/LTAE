package org.ltae.system;


import com.artemis.Aspect;
import com.artemis.EntitySubscription;
import com.artemis.annotations.One;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.BitVector;
import com.artemis.utils.IntBag;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.ltae.component.input.InputProcess;
import org.ltae.component.input.InputProcessing;

/**
 * 输入处理系统
 *
 * <p></p>
 *
 * @author WenLong
 * @version 1.0.0
 * @date 2026/6/24 10:14
 * @see InputProcessSystem
 */
@One(InputProcess.class)
public class InputProcessSystem extends IteratingSystem {
    private M<InputProcess> mInputProcess;
    @Override
    protected void process(int entityId) {
        InputProcess inputProcess = mInputProcess.get(entityId);
        if (!inputProcess.enabled) {
            return;
        }
        InputProcessing processing = inputProcess.processing;
        if (processing == null) {
            return;
        }
        if (!processing.isInited) {
            processing.init(world.getEntity(entityId));
            // 保险设置
            processing.isInited = true;
        }

        processing.update();
    }

    @Override
    protected void dispose() {
        super.dispose();
        EntitySubscription subscription = world.getAspectSubscriptionManager()
                .get(Aspect.all(InputProcess.class));
        IntBag entities = subscription.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            int i1 = entities.get(i);
            mInputProcess.get(i1).dispose();
        }

    }
}
