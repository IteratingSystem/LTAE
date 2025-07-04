package org.ltae.utils.serialize;

import com.artemis.*;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import org.ltae.manager.JsonManager;
import org.ltae.tiled.TileParam;
import org.ltae.utils.serialize.json.ComponentJson;
import org.ltae.utils.serialize.json.EntitiesJson;
import org.ltae.utils.serialize.json.EntityJson;
import org.ltae.utils.serialize.json.PropertyJson;

import java.lang.reflect.Field;

/**
 * @Auther WenLong
 * @Date 2025/7/4 11:25
 * @Description 序列化工具
 **/
public class SerializeUtils {
    private static Json json;
    static{
        json = new Json();
    }

    public static String serializeEntities(World world){
        EntitiesJson entitiesJson = new EntitiesJson();
        entitiesJson.entities = new Bag<>();

        AspectSubscriptionManager aspectSubscriptionManager = world.getSystem(AspectSubscriptionManager.class);
        EntitySubscription allEntities = aspectSubscriptionManager.get(Aspect.all());

        for (int i = 0; i < allEntities.getEntities().size(); i++) {
            int entityId = allEntities.getEntities().get(i);
            Bag<Component> allComps = new Bag<>();
            world.getEntity(entityId).getComponents(allComps);
            if (allComps.isEmpty()) {
                continue;
            }


            EntityJson entity = new EntityJson();
            entity.entityId = entityId;
            entity.components = new Bag<>();
            for (Component component : allComps) {
                Class<? extends Component> compClass = component.getClass();
                String compName = compClass.getSimpleName();
                Field[] fields = compClass.getFields();

                ComponentJson componentJson = new ComponentJson();
                componentJson.name = compName;
                componentJson.props = new Bag<>();
                for (Field field : fields) {
                    if (!field.isAnnotationPresent(Serialize.class)) {
                        continue;
                    }
                    String key = field.getName();
                    String type = field.getType().toString();
                    Object value = new Object();
                    try {
                        value = field.get(component);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    PropertyJson prop = new PropertyJson();
                    prop.key = key;
                    prop.value = value;
                    prop.type = type;

                    componentJson.props.add(prop);
                }
                entity.components.add(componentJson);
            }
            entitiesJson.entities.add(entity);
        }
        Json json = JsonManager.getInstance();
        return json.toJson(entitiesJson);
    }
}
