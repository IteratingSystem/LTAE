package org.ltae.tiled;

/**
 * @Auther WenLong
 * @Date 2025/5/13 10:04
 * @Description 创建实体时,使用到的创建模式
 **/
public enum CreateEntityMode {
    //创建为常规实体,预制件对象创建出来的实体只拥有预制件组件
    ENTITY,
    //从预制件实体创建子实体,忽略预制件组件
    PREFABRICATED;
}
