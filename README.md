# LTAE Engine

基于 [LibGDX](https://libgdx.com/) 和 [Artemis-odb](https://github.com/Artemis-ODB/Artemis-ODB) ECS框架的游戏引擎，通过 [Tiled](https://www.mapeditor.org/) 地图编辑器编辑和创建游戏实体。

## 技术栈

- **JDK**: 21
- **Tiled**: 1.11.x
- **Gradle**: 8.8

### 核心依赖

| 库 | 用途 |
|---|---|
| libgdx | 游戏框架 |
| artemis-odb | ECS架构 |
| box2dlights | 2D光照系统 |
| gdx-ai | 行为树AI |
| blade-ink | 剧本系统 |

## 项目结构

```
src/main/java/org/ltae/
├── ai/                    # AI相关（行为树任务）
│   ├── Dst.java          # 目标检测任务
│   ├── EcsLeafTask.java  # ECS叶子任务
│   ├── RandomSleep.java  # 随机等待
│   └── TimeSleep.java    # 定时等待
├── box2d/                 # Box2D物理扩展
│   ├── CategoryBits.java # 碰撞类别位
│   ├── DefFixData.java   # 碰撞 Fixture 数据
│   ├── KeyframeShapeData.java  # 关键帧形状数据
│   ├── listener/         # 碰撞监听器
│   └── setup/            # Fixture 配置
├── camera/               # 相机系统
│   └── CameraTarget.java # 相机跟随目标
├── component/            # ECS组件
│   ├── B2dBody.java      # 物理身体组件
│   ├── BTree.java        # 行为树组件
│   ├── Direction.java    # 方向组件
│   ├── EventListener.java # 事件监听组件
│   ├── Interactive.java  # 交互组件
│   ├── LayerSampling.java # 图层采样组件
│   ├── Player.java       # 玩家组件
│   ├── Pos.java          # 位置组件
│   ├── Render.java       # 渲染组件
│   ├── Script.java       # 脚本组件
│   ├── ShaderComp.java   # 着色器组件
│   ├── SoarHeight.java   # 飞行高度组件
│   ├── StateComp.java    # 状态机组件
│   ├── TileAnimation.java # 瓦片动画组件
│   ├── TileAnimations.java # 瓦片动画集
│   ├── ZIndex.java       # 渲染层级
│   └── parent/           # 组件基类
├── enums/                # 枚举定义
│   ├── HorizontalDir.java
│   ├── OrthogonalDir.java
│   └── VerticalDir.java
├── event/                # 事件系统
│   ├── B2dEvent.java     # 物理事件
│   ├── CameraEvent.java   # 相机事件
│   ├── EntityEvent.java   # 实体事件
│   ├── InteractiveEvent.java # 交互事件
│   ├── MapEvent.java      # 地图事件
│   ├── SystemEvent.java   # 系统事件
│   ├── TypeEvent.java     # 类型事件
│   ├── UIEvent.java       # UI事件
│   └── listener/         # 事件监听器
├── loader/               # 资源加载器
│   ├── CustomType.java   # 自定义类型
│   ├── EcsMapLoader.java # ECS地图加载器
│   ├── InkStoryLoader.java # Ink剧本加载器
│   └── Member.java       # 成员信息
├── manager/              # 管理系统
│   ├── AssetManager.java # 资源管理
│   ├── BundleManager.java # 国际化资源
│   ├── GameManager.java  # 游戏管理
│   ├── JsonManager.java  # JSON处理
│   ├── ScreenManager.java # 屏幕管理
│   ├── ShaderManage.java # 着色器管理
│   ├── SkinManage.java   # 皮肤管理
│   ├── StoryManager.java # 剧本管理
│   └── map/             # 地图相关管理
├── serialize/           # 序列化系统
│   ├── ComponentConfig.java
│   ├── EntityBuilder.java
│   ├── EntityDeleter.java
│   ├── EntitySerializer.java
│   ├── SerializeParam.java
│   ├── SerializeSystem.java
│   └── data/            # 序列化数据类
├── shader/              # 着色器
│   └── ShaderUniforms.java
├── state/               # 状态机
│   └── EceState.java
├── system/              # ECS系统
│   ├── AssetSystem.java     # 资源系统
│   ├── B2dSystem.java       # 物理系统
│   ├── BTreeSystem.java     # 行为树系统
│   ├── CameraSystem.java    # 相机系统
│   ├── EntityFactory.java   # 实体工厂
│   ├── KeyframeShapeSystem.java # 关键帧形状系统
│   ├── LayerSamplingSystem.java # 图层采样系统
│   ├── LightSystem.java     # 光照系统
│   ├── PosFollowBodySystem.java # 位置跟随物理体
│   ├── RenderBatchingSystem.java # 渲染批处理
│   ├── RenderFrameSystem.java # 帧渲染系统
│   ├── RenderPhysicsSystem.java # 物理渲染(调试)
│   ├── RenderTiledSystem.java # 瓦片地图渲染
│   ├── RenderUISystem.java   # UI渲染系统
│   ├── StateSystem.java     # 状态机系统
│   ├── TileAnimSystem.java  # 瓦片动画系统
│   ├── TiledMapSystem.java  # 地图系统
│   └── ZIndexSystem.java    # 渲染层级系统
├── ui/                  # UI系统
│   ├── BaseEcsUI.java   # ECS UI基类
│   ├── UIStage.java     # UI舞台
│   └── inventory/       # 背包UI组件
├── utils/               # 工具类
│   ├── InputUtils.java
│   ├── ReflectionUtils.java
│   ├── SamplingUtil.java
│   └── ShapeUtils.java
├── LtaePlugin.java      # 引擎插件入口
├── LtaePluginRule.java  # 引擎配置规则
└── LtaePluginRuleChange.java
```

## 核心特性

- **ECS架构**: 基于 Artemis-odb 的实体组件系统
- **Tiled集成**: 直接从 Tiled 地图编辑器加载实体和关卡
- **Box2D物理**: 完整的2D物理引擎支持（含光线追踪）
- **行为树AI**: 内置 gdx-ai 行为树系统
- **状态机**: 灵活的状态机组件
- **UI系统**: 基于 LibGDX 的 ECS 化 UI 系统
- **资源管理**: 自动化资源加载和缓存
- **序列化**: 实体和状态的序列化支持
- **光照系统**: 2D动态光照

## 已知问题

1. Box2D传感器只触发 `BeginContact/EndContact` 回调 - 这是正常现象
2. `FileHandle.list()` 在打包成jar后不可用

## 使用前提

1. 使用 [gdx-liftoff](https://github.com/tommyettinger/gdx-liftoff) 创建项目（需要其文件结构和 assets.txt）
2. 使用 Tiled 1.11.x 编辑器
3. 在 Tiled 中导入 `propertytypes.json`（位于 src/main/resources）
4. Tiled 中实体图层的 object 名称会自动作为实体的 TAG
5. 添加依赖: `api "com.github.IteratingSystem:LTAE:$ltaeVersion"`

## 快速开始

### 1. 资源加载

```java
// 初始化皮肤
Skin skin = SkinManage.initialize(GameRule.SKIN_PATH);

// 使用自定义AssetManager加载资源
AssetManager assetManager = AssetManager.getInstance();
assetManager.setLoaders(propertytypesPath);
assetManager.loadAssets();
```

### 2. 创建引擎插件

```java
LtaeBuilder ltaeBuilder = new LtaeBuilder()
    .setDoSleep(false)                 // Box2D是否允许休眠
    .setUIWidth(GameRule.UI_WIDTH)     // UI视口宽度
    .setUIHeight(GameRule.UI_HEIGHT)   // UI视口高度
    .setWindowWidth(GameRule.WINDOW_WIDTH)
    .setWindowHeight(GameRule.WINDOW_HEIGHT)
    .setCameraZoom(GameRule.CAMERA_ZOOM)
    .setWorldScale(GameRule.WORLD_SCALE)
    .setSkinPath(GameRule.SKIN_PATH)
    .setMapName(MAP_NAME)
    .setEntityLayerName(GameRule.ENTITY_LAYER_NAME)
    .setPhyLayerNames(GameRule.PHY_LAYER_NAMES)
    .setCompPackage(GameRule.COMP_PACKAGE)
    .setStatePackage(GameRule.STATE_PACKAGE)
    .setContactListenerPackage(GameRule.CONTACT_LISTENER_PACKAGE)
    .build();

LtaePlugin ltaePlugin = new LtaePlugin(ltaeBuilder);
```

### 3. 创建世界

```java
WorldConfiguration worldConfiguration = new WorldConfigurationBuilder()
    .with(ltaePlugin)
    .build();
World world = new World(worldConfiguration);
```

### 4. 相机跟随

```java
CameraTarget cameraTarget = new CameraTarget("PLAYER");
cameraTarget.activeHeight = 40;
cameraTarget.activeWidth = 40;
cameraTarget.eCenterX = 140;
cameraTarget.eCenterY = 90;
cameraTarget.progress = 0.03f;

CameraSystem cameraSystem = world.getSystem(CameraSystem.class);
cameraSystem.setFollowTarget(cameraTarget);
```

### 5. UI系统

```java
// 继承BaseEcsUI创建UI
public class MainUI extends BaseEcsUI {
    public MainUI(World world) {
        super(world);
        // 构建UI...
    }
}

// 添加到渲染系统
RenderUISystem uiSystem = world.getSystem(RenderUISystem.class);
MainUI mainUI = new MainUI(world);
uiSystem.addUI("mainUI", mainUI);

// 显示/隐藏
uiSystem.hideUI("mainUI");
uiSystem.showUI("mainUI");
```

## 角色翻转指南

为支持角色左右翻转，需要配置以下组件：

```java
// 1. 挂载Direction组件，在Tiled中设置horizontal为RIGHT
// 2. 状态机中同步修改方向
boolean needFlipX = direction.horizontal == HorizontalDir.LEFT;

// 3. 纹理翻转
render.flipX = needFlipX;

// 4. 物理Body翻转
b2dBody.flipX(render.keyframe.getRegionWidth());

// 5. 动画帧形状自动翻转
b2dBody.needFlipX = needFlipX;
```

## 预制对象

在tmx地图中添加预制对象（子弹、特效等）：

```java
// 通过名称创建预制实体
EntityFactory.createPrefabricatedEntity("bullet", x, y);
```

需要配置 `LtaePluginRule.PREFABRICATED_MAP_NAME`。

## 内置系统

| 系统 | 功能 |
|---|---|
| AssetSystem | 资源加载与管理 |
| B2dSystem | Box2D物理世界 |
| BTreeSystem | 行为树执行 |
| CameraSystem | 相机控制 |
| KeyframeShapeSystem | 动画帧形状 |
| LightSystem | 2D光照 |
| RenderBatchingSystem | 渲染批处理 |
| RenderFrameSystem | 精灵渲染 |
| RenderTiledSystem | 瓦片地图渲染 |
| RenderUISystem | UI渲染 |
| StateSystem | 状态机 |
| TileAnimSystem | 瓦片动画 |
| ZIndexSystem | 渲染层级排序 |

## 许可证

[MIT](LICENSE)
