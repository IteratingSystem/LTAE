package org.ltae.tiled;


//组件继承于它可以自定义加载逻辑
public interface TileCompLoader {
    void loader(TileDetails tileDetails);
}
