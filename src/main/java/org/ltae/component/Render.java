package org.ltae.component;

import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.utils.Array;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.data.EntityData;
import org.ltae.serialize.data.EntityDatum;

/**
 * @Auther WenLong
 * @Date 2025/2/12 17:11
 * @Description 渲染组件
 **/
public class Render extends SerializeComponent {
    @SerializeParam
    public boolean visible;

    public float offsetX = 0;
    public float offsetY = 0;
    public float scaleWidth = 1;
    public float scaleHeight = 1;
    public TextureRegion keyframe;

    //纹理集,用于堆叠渲染
    public Array<TextureRegion> textureSheets;
    public float sheetOffset;

    public boolean flipX = false;
    public boolean flipY = false;
    //旋转中心与旋转角度
    public float originX;
    public float originY;
    public float rotation;
    @Override
    public void reload(World world, EntityDatum entityDatum) {
        super.reload(world, entityDatum);
        if (mapObject instanceof TextureMapObject textureMapObject) {
            keyframe = textureMapObject.getTextureRegion();
            flipX = textureMapObject.getTextureRegion().isFlipX();
            flipY = textureMapObject.getTextureRegion().isFlipY();
            if (entityDatum.dataFrom == EntityData.FROM_MAP){
                visible = textureMapObject.isVisible();
            }
            int regionWidth = keyframe.getRegionWidth();
            int regionHeight = keyframe.getRegionHeight();
            MapProperties properties = mapObject.getProperties();
            float tileWidth = properties.get("width", (float) regionWidth, float.class);
            float tileHeight = properties.get("height", (float) regionHeight, float.class);
            scaleWidth = tileWidth / regionWidth;
            scaleHeight = tileHeight / regionHeight;
            originX = regionWidth / 2f;
            originY = regionHeight / 2f;
            rotation = 0;
            sheetOffset = 0;

            // ----- 修正 LibGDX TmxMapLoader 解析翻转图块时偏移一格的 Bug -----
            // 原理：引擎把 GID=N 的图块错误地映射到了 GID=N+1（右侧领格）。
            // 修正：向前回退一格。若遇到行首，则回退到上一行尾部。
            if (flipX || flipY) {
                // 获取纹理总宽度，用于计算列数
                int texWidth = keyframe.getTexture().getWidth();
                // 假设图块集无间距（spacing=0）和边距（margin=0），这是 Tiled 最常见的默认导出设置
                int tileW = regionWidth;
                int tileH = regionHeight;
                int currentX = keyframe.getRegionX();
                int currentY = keyframe.getRegionY();

                // 计算当前纹理中每行最多容纳几个图块（列数）
                int cols = texWidth / tileW;
                if (cols == 0) cols = 1; // 容错处理，防止除零

                int newX, newY;
                if (currentX - tileW >= 0) {
                    // 情况1：不在行首，直接水平左移一格
                    newX = currentX - tileW;
                    newY = currentY;
                } else {
                    // 情况2：在行首（X=0），说明右侧领格实际上位于下一行的行首，
                    // 则原本的正确图块在上一行的行尾。
                    newX = (cols - 1) * tileW;
                    newY = currentY - tileH;
                }

                // 安全检查：确保坐标有效（若 newY < 0 说明图块原本在第一行，理论上不会发生）
                if (newX >= 0 && newY >= 0) {
                    // 1. 深拷贝纹理，避免污染地图缓存
                    TextureRegion fixedRegion = new TextureRegion(keyframe);
                    // 2. 用计算出的正确坐标重新设置区域（会自动重置 UV 为正方向）
                    fixedRegion.setRegion(newX, newY, tileW, tileH);
                    // 3. 重新施加原本的翻转效果（因为 setRegion 会清除翻转状态）
                    if (flipX) fixedRegion.flip(true, false);
                    if (flipY) fixedRegion.flip(false, true);
                    // 4. 替换组件持有的纹理
                    this.keyframe = fixedRegion;
                    // 5. 关键：将翻转标志置 false。因为修正后的纹理自身已包含翻转，
                    //    外部渲染逻辑若再根据这两个字段翻转，会导致双重翻转。
                    this.flipX = false;
                    this.flipY = false;
                }
                // 若坐标越界（极少发生），则不修正，保留原样
            }
        }
    }
//    @Override
//    public void reload(World world, EntityDatum entityDatum) {
//        super.reload(world, entityDatum);
//        if (mapObject instanceof TextureMapObject textureMapObject) {
//            keyframe = textureMapObject.getTextureRegion();
//            flipX = textureMapObject.getTextureRegion().isFlipX();
//            flipY = textureMapObject.getTextureRegion().isFlipY();
//            if (entityDatum.dataFrom == EntityData.FROM_MAP){
//                visible = textureMapObject.isVisible();
//            }
//            int regionWidth = keyframe.getRegionWidth();
//            int regionHeight = keyframe.getRegionHeight();
//            MapProperties properties = mapObject.getProperties();
//            float tileWidth = properties.get("width",(float)regionWidth, float.class);
//            float tileHeight = properties.get("height",(float)regionHeight, float.class);
//            scaleWidth = tileWidth/regionWidth;
//            scaleHeight = tileHeight/regionHeight;
//            originX = regionWidth/2f;
//            originY = regionHeight/2f;
//            rotation = 0;
//            sheetOffset = 0;
//        }
//    }
}
