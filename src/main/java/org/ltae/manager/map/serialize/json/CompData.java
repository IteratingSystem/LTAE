package org.ltae.manager.map.serialize.json;


import java.util.List;

/**
 * @Auther WenLong
 * @Date 2025/7/4 10:27
 * @Description
 **/
public class CompData {
    public String name;
    public List<CompProp> props;

    public Object get (String key,Object defaultValue) {
        for (CompProp prop : props) {
            if (prop.key.equals(key)) {
                return prop.value;
            }
        }
        return defaultValue;
    }
    public boolean containsKey(String key){
        for (CompProp prop : props) {
            if (prop.key.equals(key)) {
                return true;
            }
        }
        return false;
    }
    public boolean containsKey(String[] keys){
        for (String key : keys) {
            if (!containsKey(key)) {
                return false;
            }
        }
        return true;
    }
}
