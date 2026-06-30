package org.ltae.component;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import org.ltae.LtaePluginRule;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.manager.ReflectionManager;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.data.EntityDatum;
import org.reflections.Reflections;

import java.util.Set;


/**
 * @Auther WenLong
 * @Date 2025/2/14 16:48
 * @Description 状态组件
 **/
public class StateComp extends SerializeComponent {
    private final static String TAG = StateComp.class.getSimpleName();
    @SerializeParam
    public String simpleName;
    @SerializeParam
    public String current;

    public  StateMachine<Entity,State<Entity>> machine;

    @Override
    public void reload(World world, EntityDatum entityDatum) {
        super.reload(world, entityDatum);


        ReflectionManager reflectionManager = ReflectionManager.getInstance();
        Set<Class<? extends Enum>> enumsClass = reflectionManager.getSubTypesOfWithGame(Enum.class);

        for (Class<? extends Enum> enumClass : enumsClass) {
            if (!simpleName.equals(enumClass.getSimpleName())) {
                continue;
            }

            @SuppressWarnings("unchecked")
            Enum<?> enumValue = Enum.valueOf(enumClass, current);

            if (!(enumValue instanceof State)) {
                Gdx.app.error(TAG, "This enum is not a state,SimpleName: " + simpleName + " ,Package: " + enumClass.getPackageName());
                continue;
            }
            @SuppressWarnings("unchecked")
            State<Entity> state = (State<Entity>) enumValue;
            Entity entity = world.getEntity(entityId);
            machine = new DefaultStateMachine<>(entity,state);
            break;
        }

        if (machine == null) {
            Gdx.app.error(TAG,"Failed to load state machine,SimpleName: " + simpleName);
            return;
        }
        Gdx.app.log(TAG,"Loaded state machine,SimpleName: " + simpleName);
    }


    @Override
    public void beforeSerialization() {
        super.beforeSerialization();
        current = machine.getCurrentState().toString();
    }
    public void changeState(State<Entity> state){
        machine.changeState(state);
    }
    public State<Entity> getCurrentState(){
        return machine.getCurrentState();
    }
    public boolean handleMessage(Telegram telegram){
        return machine.handleMessage(telegram);
    }
}
