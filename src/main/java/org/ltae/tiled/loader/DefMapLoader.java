package org.ltae.tiled.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.XmlReader;


/**
 * @Auther WenLong
 * @Date 2025/2/13 9:59
 * @Description 重写瓦片地图加载器,主要目的是让地图中对象的自定义属性实现层级关系
 **/
public class DefMapLoader extends TmxMapLoader {
    private final static String TAG = DefMapLoader.class.getSimpleName();
    //tiled中"自定义属性"的导出文件地址
    private final String propTypePath;
    public DefMapLoader(String propTypePath){
        this.propTypePath = propTypePath;
    }

    @Override
    protected void loadProperties (final MapProperties properties, XmlReader.Element element) {
        if (element == null) return;
        if (!element.getName().equals("properties")) return;

        for (XmlReader.Element property : element.getChildrenByName("property")) {
            final String name = property.getAttribute("name", null);
            String value = property.getAttribute("value", null);
            String type = property.getAttribute("type", null);
            if (value == null) {
                value = property.getText();
            }
            if ("object".equals(type)) {
                // Wait until the end of [loadTiledMap] to fetch the object
                try {
                    // Value should be the id of the object being pointed to
                    final int id = Integer.parseInt(value);
                    // Create [Runnable] to fetch object and add it to props
                    Runnable fetch = new Runnable() {
                        @Override
                        public void run () {
                            MapObject object = idToObject.get(id);
                            properties.put(name, object);
                        }
                    };
                    // [Runnable] should not run until the end of [loadTiledMap]
                    runOnEndOfLoadTiled.add(fetch);
                } catch (Exception exception) {
                    Gdx.app.error(TAG,"Error parsing property [" + name + "] of type \"object\" with value: [" + value + "]");
                    throw new GdxRuntimeException(
                        "Error parsing property [" + name + "] of type \"object\" with value: [" + value + "]", exception);
                }
            }else if ("class".equals(type)) {
                MapProperties childProps = new MapProperties();

                //读取自定义类型文件赋予初始值
                FileHandle propClassHandle = Gdx.files.internal(propTypePath);
                if (propClassHandle.exists()) {
                    Json json = new Json();
                    CustomType[] customTypes = json.fromJson(CustomType[].class,propClassHandle );
                    for (CustomType customType : customTypes) {
                        if (!customType.name.equals(name)) {
                            continue;
                        }
                        for (Member member : customType.members) {
                            String n = member.name;
                            String v = member.value;
                            String t = member.type;

                            Object castValue = castProperty(n, v, t);
                            childProps.put(n, castValue);
                        }
                    }
                }else {
                    Gdx.app.error(TAG,"The custom type export file in Tiled does not exist!Path:"+propTypePath);
                }


                //读取Tiled中配置的值
                for (XmlReader.Element child : property.getChildrenByName("properties")) {
                    if (child == null) continue;
                    for (XmlReader.Element cProperty : child.getChildrenByName("property")){
                        String n = cProperty.getAttribute("name", null);
                        String v = cProperty.getAttribute("value", null);
                        String t = cProperty.getAttribute("type", null);
                        Object castValue = castProperty(n, v, t);
                        childProps.put(n, castValue);
                    }
                }
                properties.put(name, childProps);
            } else {
                Object castValue = castProperty(name, value, type);
                properties.put(name, castValue);
            }
        }
    }

    @Override
    protected Object castProperty (String name, String value, String type) {
        if (type == null) {
            return value;
        }else if (type.equals("String") || type.equals("string")) {
            return value;
        }else if (type.equals("int")) {
            return Integer.valueOf(value);
        } else if (type.equals("float")) {
            return Float.valueOf(value);
        } else if (type.equals("bool")) {
            return Boolean.valueOf(value);
        } else if (type.equals("color")) {
            // Tiled uses the format #AARRGGBB
            String opaqueColor = value.substring(3);
            String alpha = value.substring(1, 3);
            return Color.valueOf(opaqueColor + alpha);
        } else {
            Gdx.app.error(TAG,"Wrong type given for property " + name + ", given : " + type + ", supported : string, bool, int, float, color");
            throw new GdxRuntimeException(
                "Wrong type given for property " + name + ", given : " + type + ", supported : string, bool, int, float, color");
        }
    }
}
