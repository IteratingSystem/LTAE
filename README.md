# LTAE
libgdx使用tiled地图编辑器编辑实体关联至artemisECS框架的引擎（Engine）插件

### 开发时各种工具版本
1. jdk:21
2. tiled:1.11.x
3. gradle:8.8

### 开发中遇到的大坑
1. Box2D中传感器只会触发BeginContact/EndContact回调,这是正常现象!
2. FileHandle.list()在编译完成后(jar包状态),不可用

### 使用前提
1. 确保使用gdx-liftoff创建项目,因为需要它的文件结构,特别是assets模块和其中的assets.txt
2. 使用tiled:1.11.x,不确保其它版本是否可行
3. 在tiled程序中导入自定义类型propertytypes.json,存在于此源码的src/main/resources中
4. 使用AssetManager.loadAssets();之前需要assetManager = AssetManager.getInstance(); assetManager.setLoaders(GameRule.PROP_TYPE_PATH);详情请看"最佳实现方案->资源加载页面"
       

### 最佳方案
##### 一.资源加载页面
1. 加载skin绘制进度条或者其它
```java
    Skin skin = SkinManage.initialize(GameRule.SKIN_PATH);
```
2. 加载默认文件,会自动将assets模块内的特殊后缀名文件加载,依赖于assets.txt,比如.tmx的文件会被加载为TiledMap;
```java
    //使用工具里的AssetManager,并非libgdx内的AssetManager
    AssetManager assetManager = AssetManager.getInstance();
    //设置所有的加载器,TileMapLoader需要一个propertytypes.json,所以要传入一个path
    //具体作用是给Tiled程序中没有填入值的组件匹配上propertytypes.json中的默认值
    assetManager.setLoaders(propertytypesPath);
    //直接加载,目前支持"tmx(瓦片地图),tree(行为树)"
    assetManager.loadAssets();
```
