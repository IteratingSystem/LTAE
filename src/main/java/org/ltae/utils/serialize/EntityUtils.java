package org.ltae.utils.serialize;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import org.ltae.component.SerializeComponent;
import org.ltae.tiled.SerializeParam;
import org.ltae.utils.ReflectionUtils;
import org.ltae.utils.serialize.json.ComponentJson;
import org.ltae.utils.serialize.json.EntitiesJson;
import org.ltae.utils.serialize.json.EntityJson;
import org.ltae.utils.serialize.json.PropertyJson;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @Auther WenLong
 * @Date 2025/7/4 17:23
 * @Description
 **/
public class EntityUtils {
    public static void createAll(World world, EntitiesJson entitiesJson,String[] compPackages){
        TagManager tagManager = world.getSystem(TagManager.class);

        for (EntityJson entityJson : entitiesJson.entities) {
            //创建
            int entityId = world.create();
            entityJson.entityId = entityId;
            //注册tag
            if (!"".equals(entityJson.name)) {
                tagManager.register(entityJson.name,entityId);
            }
            //注册组件
            Bag<ComponentJson> components = entityJson.components;
            Set<Class<? extends Component>> classes = ReflectionUtils.getClasses(compPackages, Component.class);
            for (Class<? extends Component> aClass : classes) {
                String simpleName = aClass.getSimpleName();
                for (ComponentJson componentJson : components) {
                    if (!simpleName.equals(componentJson.name)) {
                        continue;
                    }
                    //通过类对象创建组件Mapper
                    ComponentMapper<? extends Component> mapper = world.getMapper(aClass);
                    if (mapper == null) {
                        break;
                    }
                    //通过组件Mapper创建组件
                    Component component = mapper.create(entityId);
                    //写入默认值
                    Bag<PropertyJson> props = componentJson.props;
                    for (PropertyJson prop : props) {
                        String key = prop.key;
                        Object value = prop.value;
                        try {
                            Field declaredField = aClass.getDeclaredField(key);
                            if (!declaredField.isAnnotationPresent(SerializeParam.class)) {
                                continue;
                            }
                            declaredField.set(component,value);
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    //执行reload
                    if (component instanceof SerializeComponent serializeComponent) {
                        serializeComponent.reload(world,entityJson);
                    }
                }
            }
        }
    }
}
