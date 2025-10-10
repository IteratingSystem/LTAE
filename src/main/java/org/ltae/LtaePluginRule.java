package org.ltae;

import com.artemis.Component;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.component.Pos;
import org.ltae.component.Render;
import org.ltae.component.ZIndex;

/**
 * @Auther WenLong
 * @Date 2025/3/17 17:05
 * @Description
 **/
public class LtaePluginRule {


    public static ObjectMap<String, String> ENTITY_LAYERS = new ObjectMap<>();
    public static ObjectMap<String, String> PHY_LAYERS = new ObjectMap<>();
    //ui视图宽高
    public static int UI_WIDTH = 640;
    public static int UI_HEIGHT = 480;
    public static float GAME_WIDTH = 640;      // 窗口宽度
    public static float GAME_HEIGHT = 480;     // 窗口高度
    public static float CAMERA_ZOOM = 1.0f;      //相机放大比例
    public static float WORLD_SCALE = 1.0f;      // 世界缩放比例
    public static float G_X = 0.0f;              // 默认横向重力
    public static float G_Y = -9.8f;             // 默认纵向重力（模拟地球重力）
    public static boolean B2D_SLEEP = false;         //box2d是否休眠
    public static boolean COMB_TILE = true;
    public static String MAP_NAME = "defaultMap"; // 默认加载的地图名称
    public static String PREFABRICATED_MAP_NAME = "defaultMap"; // 预制件地图名称
    public static String PHY_LAYER = "physicsLayer"; // 默认物理图层
    public static String ENTITY_LAYER = "entities";       // 默认实体图层
    public static String COMPONENT_PKG = "com.game.component";    //组件包路径
    public final static String LTAE_COMPONENT_PKG = "org.ltae.component";    //组件包路径
    public static String STATE_PKG = "com.game.state";    //状态机包路径
    public static String B2D_LISTENER_PKG = "com.game.contact";   // BOX2D监听器包路径
    public static String ON_EVENT_PKG = "com.game.event.on";//事件接收器包路径
    public static String SHADER_UNIFORMS_PKG = "com.game.shader";//shader传参类包名
    public static String SKIN_PATH = "skin/main.json"; //skin皮肤路劲
    //就算没有维护也自动创建的组件
    public static Class[] AUTO_COMP_CLASSES = {Pos.class, Render.class, ZIndex.class};
    private LtaePluginRule(){}
}