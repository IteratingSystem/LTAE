package org.ltae.component.input;


import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.manager.ReflectionManager;
import org.ltae.manager.input.InputManager;
import org.ltae.serialize.PostLoad;
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

public class InputProcess extends SerializeComponent implements Disposable {
    // 是否开启操控
    @SerializeParam
    public boolean enabled;
    @SerializeParam
    public String simpleName;

    public transient InputProcessing processing;

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("enabled", enabled);
        json.writeValue("simpleName", simpleName);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        enabled = jsonData.getBoolean("enabled", false);
        simpleName = jsonData.has("simpleName") ? jsonData.getString("simpleName") : null;
    }

    @PostLoad
    public void postLoadInputProcess(World world) {
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
        Gdx.app.log(getTag(), "Instantiated InputProcessing: " + simpleName);
    }

    @Override
    public void dispose() {
        if (processing != null) {
            processing.dispose();
            processing = null;
        }
    }
}
