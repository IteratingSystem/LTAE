package org.ltae.component.inter;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import org.ltae.component.inter.listener.ExclusiveInteract;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.component.inter.listener.OnInteractListener;
import org.ltae.manager.ReflectionManager;
import org.ltae.serialize.PostLoad;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.data.EntityDatum;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:35
 * @Description 交互组件
 **/
public class Interactive extends SerializeComponent {
    @SerializeParam
    public String simpleName;


    public transient OnInteractListener onInteractListener;
    public boolean isExclusiveInteract;

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("simpleName", simpleName);
        json.writeValue("isExclusiveInteract", isExclusiveInteract);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        simpleName = jsonData.has("simpleName") ? jsonData.getString("simpleName") : null;
        isExclusiveInteract = jsonData.getBoolean("isExclusiveInteract", false);
    }

    @PostLoad
    public void postLoadInteractive(World world) {
        ReflectionManager reflectionManager = ReflectionManager.getInstance();
        Class<? extends OnInteractListener> aClass = reflectionManager
                .getSubTypesOfWithGame(OnInteractListener.class)
                .stream()
                .filter(c -> c.getSimpleName().equals(simpleName))
                .findFirst()
                .orElse(null);
        if (aClass == null) {
            Gdx.app.error(getTag(),"Failed to find OnInteractListener, simpleName: " + simpleName);
            return;
        }

        onInteractListener = reflectionManager.createObject(
                aClass,
                new Class[]{Entity.class},
                new Object[]{world.getEntity(entityId)}
        );
        if (onInteractListener == null) {
            Gdx.app.error(getTag(),"Failed to create OnInteractListener, simpleName: " + simpleName);
            return;
        }
        Gdx.app.debug(getTag(),"onInteractListener: " + onInteractListener.getClass().getSimpleName());

        isExclusiveInteract = onInteractListener.getClass().isAnnotationPresent(ExclusiveInteract.class);
    }
}
