package org.ltae.component.inter;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import org.ltae.component.inter.listener.ExclusiveInteract;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.component.inter.listener.OnInteractListener;
import org.ltae.manager.ReflectionManager;
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


    public OnInteractListener onInteractListener;
    public boolean isExclusiveInteract;
    @Override
    public void reload(World world, EntityDatum entityDatum) {
        super.reload(world, entityDatum);
        ReflectionManager reflectionManager = ReflectionManager.getInstance();
        Class<? extends OnInteractListener> aClass = reflectionManager
                .getSubTypesOfWithGame(OnInteractListener.class)
                .stream()
                .findFirst()
                .filter(c -> c.getSimpleName().equals(simpleName))
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
