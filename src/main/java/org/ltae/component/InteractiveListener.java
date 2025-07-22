package org.ltae.component;

import com.artemis.World;
import org.ltae.manager.map.serialize.SerializeParam;
import org.ltae.manager.map.serialize.json.EntityData;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:35
 * @Description 交互组件
 **/
public class InteractiveListener extends EventListener {
    @SerializeParam
    public String simpleName;

    @Override
    public void reload(World world, EntityData entityData) {
        super.reload(world, entityData);
        registerEvent(simpleName);
    }
}
