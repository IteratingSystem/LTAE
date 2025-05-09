package org.ltae;



/**
 * @Auther WenLong
 * @Date 2025/3/17 17:05
 * @Description
 **/
public class LtaeBuilder {
    //ui视图宽高
    private int uiWidth = 640;
    private int uiHeight = 480;
    private float windowWidth = 640;      // 默认窗口宽度
    private float windowHeight = 480;     // 默认窗口高度
    private float cameraZoom = 1.0f;      // 默认缩放比例
    private float worldScale = 1.0f;      // 默认世界缩放比例
    private float gx = 0.0f;              // 默认横向重力
    private float gy = -9.8f;             // 默认纵向重力（模拟地球重力）
    private boolean doSleep = false;         //box2d

    private String mapName = "defaultMap"; // 默认加载的地图名称
    private String[] phyLayerNames = {"physicsLayer"}; // 默认物理图层
    private String skinPath = "skin/main.json";
    private String entityLayerName = "entities";       // 默认实体图层
    private String compPackage = "com.game.component";    // 默认组件包路径
    private String statePackage = "com.game.state";    // 默认组件包路径
    private String contactListenerPackage = "com.game.contact";    // 默认组件包路径

    // 构造器
    public LtaeBuilder() {
        // 可以在这里设置默认值
    }

    // 流式创建方法
    public LtaeBuilder setWindowWidth(float windowWidth) {
        this.windowWidth = windowWidth;
        return this;
    }

    public LtaeBuilder setWindowHeight(float windowHeight) {
        this.windowHeight = windowHeight;
        return this;
    }

    public LtaeBuilder setCameraZoom(float cameraZoom) {
        this.cameraZoom = cameraZoom;
        return this;
    }
    public LtaeBuilder setDoSleep(boolean doSleep) {
        this.doSleep = doSleep;
        return this;
    }

    public LtaeBuilder setWorldScale(float worldScale) {
        this.worldScale = worldScale;
        return this;
    }
    public LtaeBuilder setUIWidth(int uiWidth) {
        this.uiWidth = uiWidth;
        return this;
    }
    public LtaeBuilder setUIHeight(int uiHeight) {
        this.uiHeight = uiHeight;
        return this;
    }
    public LtaeBuilder setSkinPath(String skinPath) {
        this.skinPath = skinPath;
        return this;
    }
    public int getUIWidth() {
        return uiWidth;
    }
    public int getUIHeight() {
        return uiHeight;
    }
    public LtaeBuilder setGx(float gx) {
        this.gx = gx;
        return this;
    }

    public LtaeBuilder setGy(float gy) {
        this.gy = gy;
        return this;
    }

    public LtaeBuilder setMapName(String mapName) {
        this.mapName = mapName;
        return this;
    }

    public LtaeBuilder setPhyLayerNames(String[] phyLayerNames) {
        this.phyLayerNames = phyLayerNames;
        return this;
    }

    public LtaeBuilder setEntityLayerName(String entityLayerName) {
        this.entityLayerName = entityLayerName;
        return this;
    }

    public LtaeBuilder setCompPackage(String compPackage) {
        this.compPackage = compPackage;
        return this;
    }
    public LtaeBuilder setStatePackage(String statePackage) {
        this.statePackage = statePackage;
        return this;
    }
    public LtaeBuilder setContactListenerPackage(String contactListenerPackage) {
        this.contactListenerPackage = contactListenerPackage;
        return this;
    }

    // 构建方法
    public LtaeBuilder build() {
        // 可以在这里进行一些验证或初始化操作
        return this;
    }

    // Getter 方法
    public float getWindowWidth() {
        return windowWidth;
    }

    public float getWindowHeight() {
        return windowHeight;
    }

    public float getCameraZoom() {
        return cameraZoom;
    }

    public float getWorldScale() {
        return worldScale;
    }

    public float getGx() {
        return gx;
    }

    public float getGy() {
        return gy;
    }


    public String getMapName() {
        return mapName;
    }

    public String[] getPhyLayerNames() {
        return phyLayerNames;
    }

    public String getEntityLayerName() {
        return entityLayerName;
    }

    public String getCompPackage() {
        return compPackage;
    }
    public String getStatePackage() {
        return statePackage;
    }
    public String getContactListenerPackage() {
        return contactListenerPackage;
    }

    public boolean getDoSleep() {
        return doSleep;
    }

    public String getSkinPath() {
        return skinPath;
    }
}