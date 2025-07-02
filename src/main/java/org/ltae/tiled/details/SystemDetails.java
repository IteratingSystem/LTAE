package org.ltae.tiled.details;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.ObjectMap;
import net.mostlyoriginal.api.event.common.EventSystem;
import org.ltae.system.AssetSystem;

import java.util.Set;


/**
 * @Auther WenLong
 * @Date 2025/2/17 14:30
 * @Description 在将地图中对象创建为实体的过程中, 用于传参的对象
 **/
public class SystemDetails {
    public float worldScale;
    public World world;
    public EventSystem eventSystem;
    public TiledMap tiledMap;
    public String entityLayer;
    public Bag<Class<? extends Component>> autoCompClasses;
    public String statePkg;
    public String b2dListenerPkg;
    public String componentPkg;
    public String onEventPkg;

}
