package org.ltae.manager.map.serialize;

import com.artemis.Component;
import com.artemis.utils.Bag;

/**
 * @Auther WenLong
 * @Date 2025/7/9 17:19
 * @Description
 **/
public class ComponentConfig {
    public String[] compPackages;
    public Bag<Class<? extends Component>> autoCompClasses;
}
