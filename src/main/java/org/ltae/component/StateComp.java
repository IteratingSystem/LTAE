package org.ltae.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import org.ltae.tiled.TileCompLoader;
import org.ltae.tiled.TileDetails;
import org.ltae.tiled.TileParam;


/**
 * @Auther WenLong
 * @Date 2025/2/14 16:48
 * @Description 状态组件
 **/
public class StateComp extends Component implements TileCompLoader {
    private final static String TAG = StateComp.class.getSimpleName();
    @TileParam
    public String sampleName;
    @TileParam
    public String current;

    public StateMachine<Entity,State<Entity>> machine;

    @Override
    public void loader(TileDetails tileDetails) {
        String className = tileDetails.statePackage + "." + sampleName;
        Class<?> stateClass;
        try {
            stateClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            Gdx.app.error(TAG,"Failed to get class with name:"+className);
            return;
        }
        Class<Enum> enumClass = (Class<Enum>) stateClass.asSubclass(stateClass);
        Enum enumValue = Enum.valueOf(enumClass, current);

        Entity entity = tileDetails.entity;

        State state = (State)enumValue;
        machine = new DefaultStateMachine<>(entity,state);
    }
}
