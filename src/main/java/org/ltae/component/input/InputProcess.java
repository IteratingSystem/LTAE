package org.ltae.component.input;


import com.artemis.World;
import com.badlogic.gdx.Gdx;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.manager.ReflectionManager;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.data.EntityDatum;
import java.util.Set;

/**
 * 操控组件
 *
 * <p>在状态机或者其它地方处理输入控制实体,可以使用此组件来控制相应的代码是否生效</p>
 *
 * @author WenLong
 * @version 1.0.0
 * @date 2026/6/23 11:52
 * @see InputProcess
 */
public class InputProcess extends SerializeComponent {
    // 是否开启操控
    @SerializeParam
    public boolean enabled;
    @SerializeParam
    public String simpleName;

    public InputProcessing processing;

    @Override
    public void reload(World world, EntityDatum entityDatum) {
        super.reload(world, entityDatum);

        ReflectionManager reflectionManager = ReflectionManager.getInstance();
        Set<Class<? extends InputProcessing>> subTypes = reflectionManager.getSubTypesOfWithGame(InputProcessing.class);
        Class<? extends InputProcessing> targetClass = subTypes.stream()
                .filter(c -> simpleName.equals(c.getSimpleName()))
                .findFirst()
                .orElse(null);

        if (targetClass == null) {
            Gdx.app.error(getTag(), "No suitable InputProcess found for " + simpleName);
            return;
        }
        processing = reflectionManager.createObject(
                targetClass,
                null,
                null
        );

        if (processing == null) {
            Gdx.app.error(getTag(), "Failed to create processing,SimpleName: " + simpleName);
            return;
        }
        Gdx.app.log(getTag(), "Instantiated InputProcessing: " + processing.getClass().getName());


    }
}
