package org.ltae.manager.map.serialize.data;


import java.util.List;

/**
 * @Auther WenLong
 * @Date 2025/7/4 10:27
 * @Description
 **/
public class CompDatum {
    public String name;
    public List<CompProp> props;


    //获取该组件某key的值,如果没有这个key则直接返回defaultValue
    public Object getValue (String key,Object defaultValue) {
        for (CompProp prop : props) {
            if (prop.key.equals(key)) {
                return prop.value;
            }
        }
        return defaultValue;
    }
    //设置该组件某key的值,如果已经存在值则替换,如果没有则新增
    public boolean setValue (String key,Object value) {
        for (CompProp prop : props) {
            if (prop.key.equals(key)) {
                prop.value = value;
                return true;
            }
        }
        CompProp compProp = new CompProp();
        compProp.key = key;
        compProp.value = value;
        compProp.type = value.getClass().toString();
        props.add(compProp);
        return true;
    }
    public boolean containsKey(String key){
        for (CompProp prop : props) {
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
