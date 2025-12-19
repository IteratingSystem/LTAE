package org.ltae.component;

import com.artemis.World;
import org.ltae.manager.map.serialize.SerializeParam;
import org.ltae.manager.map.serialize.data.EntityDatum;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:35
 * @Description 交互组件
 **/
public class Interactive extends EventListener {
    @SerializeParam
    public String simpleName;

    @Override
    public void reload(World world, EntityDatum entityDatum) {
        super.reload(world, entityDatum);
        registerEvent(simpleName);
    }
}
