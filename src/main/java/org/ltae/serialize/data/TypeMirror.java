package org.ltae.serialize.data;

/**
 * @author: ClassMirror
 * @date: 2025/12/26
 * @description: 对于类或者类型的序列化对象, 拥有一个类名(或类型名), 以及属性列表
 */

public class TypeMirror {

    public String simpleName;
    public Properties properties;

    //获取该组件某key的值
    public Object getValue (String key) {
        for (Property prop : properties) {
            if (prop.key.equals(key)) {
                return prop.value;
            }
        }
        return null;
    }
    //获取该组件某key的值,如果没有这个key则直接返回defaultValue
    public Object getValue (String key,Object defaultValue) {
        for (Property prop : properties) {
            if (prop.key.equals(key)) {
                return prop.value;
            }
        }
        return defaultValue;
    }
    //设置该组件某key的值,如果已经存在值则替换,如果没有则新增
    public boolean setValue (String key,Object value) {
        for (Property prop : properties) {
            if (prop.key.equals(key)) {
                prop.value = value;
                return true;
            }
        }
        Property property = new Property();
        property.key = key;
        property.value = value;
        property.type = value.getClass().toString();
        properties.add(property);
        return true;
    }
    public boolean containsKey(String key){
        for (Property prop : properties) {
            if (prop.key.equals(key)) {
                return true;
            }
        }
        return false;
    }
    public boolean containsKeys(String[] keys){
        for (String key : keys) {
            if (!containsKey(key)) {
                return false;
            }
        }
        return true;
    }
}
