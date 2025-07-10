package org.ltae.component;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import org.ltae.LtaePluginRule;
import org.ltae.manager.map.serialize.SerializeParam;
import org.ltae.manager.map.serialize.json.EntityJson;
import org.reflections.Reflections;

import java.util.Set;


/**
 * @Auther WenLong
 * @Date 2025/2/14 16:48
 * @Description 状态组件
 **/
public class StateComp extends SerializeComponent{
    private final static String TAG = StateComp.class.getSimpleName();
    @SerializeParam
    public String simpleName;
    @SerializeParam
    public String current;

    public  StateMachine<Entity,State<Entity>> machine;

    @Override
    public void reload(World world, EntityJson entityJson) {
        super.reload(world,entityJson);
        String statePkg = LtaePluginRule.STATE_PKG;
        Reflections reflections = new Reflections(statePkg);
        Set<Class<? extends Enum>> enumsClass = reflections.getSubTypesOf(Enum.class);
        for (Class<? extends Enum> enumClass : enumsClass) {
            if (!simpleName.equals(enumClass.getSimpleName())) {
                continue;
            }
            Enum enumValue = Enum.valueOf(enumClass, current);
            Entity entity = world.getEntity(entityId);
            State state = (State)enumValue;
            machine = new DefaultStateMachine<>(entity,state);
            break;
        }
    }
}
