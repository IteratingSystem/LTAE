package org.ltae.system;


import com.artemis.Aspect;
import com.artemis.EntitySubscription;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.ltae.component.CtrlComp;
import org.ltae.component.StateComp;
import org.ltae.manager.input.InputManager;
import org.ltae.manager.input.LongPressListener;
import org.ltae.massage.telegram.InputTelegram;

/**
 * 操控系统
 *
 * <p>给挂载了Ctrl组件的实体,发送实时输入数据</p>
 *
 * @author WenLong
 * @date 2026/6/23 11:48
 * @Version 1.0.0
 * @see CtrlSystem
 */

@All({CtrlComp.class, StateComp.class})
public class CtrlSystem extends IteratingSystem {
    private final static String TAG = CtrlSystem.class.getName();
    private int ctrlMassage;

    private M<CtrlComp> mCtrlComp;
    private M<StateComp> mStateComp;

    public CtrlSystem(int ctrlMassage) {
        this.ctrlMassage = ctrlMassage;
    }

    @Override
    protected void initialize() {
        super.initialize();
        InputManager.addLongPressListener(new LongPressListener() {
            @Override
            public void onLongPress(int keycode) {
                sendTelegram(InputTelegram.LONG_PRESS, keycode);
            }

            @Override
            public void onShortPress(int keycode) {
                sendTelegram(InputTelegram.SHOUT_PRESS, keycode);
            }
        });
    }

    private void sendTelegram(int pressType,int keycode){
        EntitySubscription subscription = world.getAspectSubscriptionManager()
                .get(Aspect.all(CtrlComp.class, StateComp.class));
        IntBag entityIds = subscription.getEntities();
        for (int i = 0; i < entityIds.size(); i++) {
            int entityId = entityIds.get(i);
            sendTelegram(entityId,pressType,keycode);
        }
    }
    private void sendTelegram(int entityId,int pressType,int keycode){
        if (!mCtrlComp.has(entityId)) {
            Gdx.app.log(TAG, "Failed to 'sendTelegram',This entity not has component with 'CtrlComp',entityId: " + entityId);
            return;
        }
        CtrlComp ctrlComp = mCtrlComp.get(entityId);
        if (!ctrlComp.enabled) {
            return;
        }

        if (!mStateComp.has(entityId)) {
            Gdx.app.error(TAG, "Failed to 'sendTelegram',This entity not has component with 'StateComp',entityId: " + entityId);
            return;
        }

        InputTelegram inputTelegram = new InputTelegram(pressType,keycode);
        inputTelegram.message = ctrlMassage;

        StateComp stateComp = mStateComp.get(entityId);
        stateComp.handleMessage(inputTelegram);
    }

    
    @Override
    protected void process(int entityId) {
    }
}
