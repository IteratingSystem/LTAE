package org.ltae.component;

import com.artemis.Entity;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import org.ltae.tiled.TiledSerializeLoader;
import org.ltae.tiled.SerializeParam;
import org.ltae.tiled.details.EntityDetails;
import org.ltae.tiled.details.SystemDetails;
import org.reflections.Reflections;

import java.util.Set;


/**
 * @Auther WenLong
 * @Date 2025/2/14 16:48
 * @Description 状态组件
 **/
public class StateComp extends SerializeComponent implements TiledSerializeLoader {
    private final static String TAG = StateComp.class.getSimpleName();
    @SerializeParam
    public String simpleName;
    @SerializeParam
    public String current;

    public  StateMachine<Entity,State<Entity>> machine;

    @Override
    public void loader(SystemDetails systemDetails, EntityDetails entityDetails) {
        String statePkg = systemDetails.statePkg;
        Reflections reflections = new Reflections(statePkg);
        Set<Class<? extends Enum>> enumsClass = reflections.getSubTypesOf(Enum.class);
        for (Class<? extends Enum> enumClass : enumsClass) {
            if (!simpleName.equals(enumClass.getSimpleName())) {
                continue;
            }
            Enum enumValue = Enum.valueOf(enumClass, current);
            Entity entity = systemDetails.world.getEntity(entityDetails.entityId);
            State state = (State)enumValue;
            machine = new DefaultStateMachine<>(entity,state);
            break;
        }
    }
}
