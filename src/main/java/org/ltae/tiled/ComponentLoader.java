package org.ltae.tiled;


import org.ltae.tiled.details.EntityDetails;
import org.ltae.tiled.details.SystemDetails;

//组件继承于它可以自定义加载逻辑
public interface ComponentLoader {
    void loader(SystemDetails systemDetails, EntityDetails entityDetails);
}
