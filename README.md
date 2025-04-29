# LTAE
libgdx使用tiled地图编辑器编辑实体关联至artemisECS框架的引擎（Engine）插件

# 开发时各种工具版本
##### jdk:21
##### tiled:1.11.x
##### gradle:8.8

# 开发中遇到的大坑
Box2D中传感器只会触发BeginContact/EndContact回调,这是正常现象!

# 使用前提
##### 1.确保使用gdx-liftoff创建项目,因为需要它的文件结构,特别是assets模块和其中的assets.txt
##### 2.使用tiled:1.11.x,不确保其它版本是否可行
##### 3.在tiled程序中导入自定义类型propertytypes.json,存在于此源码的src/main/resources中


# 部分说明
1.在tiled中导入propertytypes.json文件,即可使用此工具所有的组件(src/main/resources/propertytypes.json)
2.创建自定义组件:新增组件后继承com.artemis.Component并且实现org.ltae.tiled.TileCompLoader,组件中需要从tiled中直接获取同名的属性需要用@TileParam注解

