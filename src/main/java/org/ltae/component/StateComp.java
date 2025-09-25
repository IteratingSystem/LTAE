package org.ltae.component;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import org.ltae.LtaePluginRule;
import org.ltae.manager.map.serialize.SerializeParam;
import org.ltae.manager.map.serialize.json.EntityData;
import org.reflections.Reflections;

import java.util.Set;
import java.util.stream.Collectors;


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
    public void reload(World world, EntityData entityData) {
        super.reload(world, entityData);
        String statePkg = LtaePluginRule.STATE_PKG;
        Reflections reflections = new Reflections(statePkg);
        Set<Class<? extends Enum>> enumsClass = reflections.getSubTypesOf(Enum.class);
        for (Class<? extends Enum> enumClass : enumsClass) {
            if (!simpleName.equals(enumClass.getSimpleName())) {
                continue;
            }
            @SuppressWarnings("unchecked")// 仅此处抑制；已知安全
            Enum<?> enumValue = Enum.valueOf(enumClass, current);
            /* --- 运行时检查是否真的实现了 State<Entity> --- */
            if (!(enumValue instanceof State)) {
                Gdx.app.error(TAG, "Enum constant '" + current
                        + "' does not implement State<Entity>");
            }
            @SuppressWarnings("unchecked")// 此时安全
            State<Entity> state = (State<Entity>) enumValue;
            Entity entity = world.getEntity(entityId);
            machine = new DefaultStateMachine<>(entity,state);
            break;
        }
    }
}
