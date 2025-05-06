# LTAE
libgdx使用tiled地图编辑器编辑实体关联至artemisECS框架的引擎（Engine）插件

### 开发时各种工具版本
1. jdk:21
2. Tiled:1.11.x
3. gradle:8.8

### 开发中遇到的大坑
1. Box2D中传感器只会触发BeginContact/EndContact回调,这是正常现象!
2. FileHandle.list()在编译完成后(jar包状态),不可用

### 使用前提
1. 确保使用gdx-liftoff创建项目,因为需要它的文件结构,特别是assets模块和其中的assets.txt
2. 使用Tiled:1.11.x,不确保其它版本是否可行
3. 在Tiled程序中导入自定义类型propertytypes.json,存在于此源码的src/main/resources中
4. 在Tiled程序中为实体图层中object指定的名字会自动读取为实体的TAG
5. 使用`AssetManager.loadAssets();`之前需要
```java
    assetManager = AssetManager.getInstance(); 
    assetManager.setLoaders(GameRule.PROP_TYPE_PATH);
    //详情请看"最佳实现方案->资源加载页面"
```   
并且需要在idea启动项目中配置资源路径为assets模块

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
    //直接加载,会自动将assets模块内的特殊后缀名文件加载,目前支持"tmx(瓦片地图),tree(行为树)"
    assetManager.loadAssets();
```

##### 二.游戏页面
1. 配置游戏环境以创建此插件,路径类都是相对路径,相对于assets模块
```java
    LtaeBuilder ltaeBuilder = new LtaeBuilder()
        .setDoSleep(false)  //box2d世界是否开启静止物体休眠计算
        //UI页面视图的宽高,内容会拉扯至整个窗口大小
        .setUIWidth(GameRule.UI_WIDTH)  
        .setUIHeight(GameRule.UI_HEIGHT)
        //窗口大小及摄像头放大系数(越大则离目标越近则图像越大)
        .setWindowWidth(GameRule.WINDOW_WIDTH) 
        .setWindowHeight(GameRule.WINDOW_HEIGHT)
        .setCameraZoom(GameRule.CAMERA_ZOOM)
        //世界缩放比例
        .setWorldScale(GameRule.WORLD_SCALE)
        //皮肤路径
        .setSkinPath(GameRule.SKIN_PATH)
        //需要加载的地图名称:文件名(非路径,无后缀)
        .setMapName(MAP_NAME)
        //地图中加载实体的图层,自动读取此图层的Object到实体,Object的默认name会被载入为实体的TAG
        .setEntityLayerName(GameRule.ENTITY_LAYER_NAME)
        //地图中默认创建静态墙体的图层,物理过滤位为I类
        .setPhyLayerNames(GameRule.PHY_LAYER_NAMES)
        //配置自定义组件包
        .setCompPackage(GameRule.COMP_PACKAGE)
        //配置自定义状态机包
        .setStatePackage(GameRule.STATE_PACKAGE)
        //配置自定义碰撞监听包
        .setContactListenerPackage(GameRule.CONTACT_LISTENER_PACKAGE)
        .build();
    //创建插件
    LtaePlugin ltaePlugin = new LtaePlugin(ltaeBuilder);
```
2. 创建世界
```java
    //此插件中已经包含了如下外部插件:
    //ExtendedComponentMapperPlugin 拓展组件映射
    //ProfilerPlugin    监控查询
    //TagManager    标签管理器
    //PlayerManager 玩家管理器
    //TeamManager   团队管理器
    //EntityLinkManager 实体连接管理器
    //EventSystem   事件总线
    WorldConfiguration worldConfiguration = new WorldConfigurationBuilder()
        .with(ltaePlugin)
        .build();
    world = new World(worldConfiguration);
```
3. 相机跟随目标,实现流程是获取CameraSystem,给它传入一个CameraTarget对象,CameraTarget对象包含要跟随的实体以及相机活动区域和偏倚量等信息.
```java
    //创建CameraTarget是指定实体的TAG
    CameraTarget cameraTarget = new CameraTarget("PLAYER");
    //活动区域大小,实体在此区域内不直接跟随
    cameraTarget.activeHeight = 40;
    cameraTarget.activeWidth = 40;
    //偏移量
    cameraTarget.eCenterX = 140;
    cameraTarget.eCenterY = 90;
    //跟随速度
    cameraTarget.progress = 0.03f;
    
    //获取CameraSystem并传入cameraTarget
    CameraSystem cameraSystem = world.getSystem(CameraSystem.class);
    cameraSystem.setFollowTarget(cameraTarget);
```

##### 三.UI系统
1. 制作UI页面:创建一个类,继承于`org.ltae.ui.BaseUI`,其本质是一个Table,包含了ECS的world及一些相关信息为属性,其构造如下:
```java
    public BaseUI(World world){
        this.world = world;
        tagManager = world.getSystem(TagManager.class);
        assetSystem = world.getSystem(AssetSystem.class);
        skin = assetSystem.skin;
    }
```
子类中super(world)则可以直接使用其属性
2. 将制作的UI传入RenderUISystem,例(假设制作的UI为MainUi):
```java
    RenderUISystem uiSystem = world.getSystem(RenderUISystem.class);
    MainUI mainUI = new MainUI(world);
    //添加UI时为其指定一个自定义名称,在后面方便其它操作
    uiSystem.addUI("customUiName",mainUI);
```
传入RenderUISystem后则会根据ui本身的visible属性来确定是否显示,也可以使用:
```java
    uiSystem.hideUI("customUiName");
    uiSystem.showUI("customUiName");
```
来显示和隐藏相应名称的UI;


##### 四.角色左右翻转
1. 场景说明:在角色向左和向右移动时,通常用的是同一套素材,需要将素材左右翻转;不仅如此,还需要将其物理Body中的形状进行翻转;还有一点是动画帧中包含的翻转;
2. 最佳实践如下:
   1. 对于需要左右翻转的角色,判断是否需要翻转的最佳实践:
      1. 给它挂载上Direction组件,将其horizontal设置为当前的方向(LEFT或者RIGHT)
      2. 在状态机中改变方向后,同步修改Direction.horizontal
      3. 判断是否需要翻转:当素材默认向右时`Direction.horizontal==HorizontalDir.LEFT`则需要翻转,反之亦然;
   2. 对于纹理的翻转,直接设置`Render.flipX = true;`代表需要翻转,设置后纹理将以翻转X的状态实时渲染;
   3. 对于物理Body中形状的翻转,使用B2dBody组件的flipX(float regionWidth)方法可翻转,其原理是将形状移动到翻转后的位置模拟的翻转,目前只对其内部的圆形和矩形生效:`B2dBody.flipX(float regionWidth);`
   4. 动画帧中的形状(比如攻击帧的攻击传感器),其会在创建阶段自动翻转,需要设置`B2dBody.needFlipX=true`则创建时自动翻转;
   5. 完整示例:
   ```java
        //假设素材默认是向右的方向
        //则在Tiled中挂载Direction组件,配置其horizontal值为RIGHT
        //在状态机中会改变方向的操作中同步修改Direction.horizontal,比如向左移动与向右移动中修改其方向
        //使用Direction.horizontal与素材的方向对比判断是否需要翻转
        boolean needFlipX = direction.horizontal==HorizontalDir.LEFT;
        //翻转纹理
        render.flipX = needFlipX;
        //翻转物理Body
        b2dBody.flipX(render.keyframe.getRegionWidth());
        //设置needFlipX,帧形状生成时自动判断是否需要翻转
        b2dBody.needFlipX = needFlipX;
   ```