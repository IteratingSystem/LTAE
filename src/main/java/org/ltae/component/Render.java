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

            // ----- 修正 LibGDX TmxMapLoader 解析带翻转图块时偏移一格的 Bug -----
            // 当图块带有翻转标志时，引擎错误地将 GID 映射到右侧领格。
            // 这里检测到翻转，则深拷贝一份当前纹理，手动左移一格，并重新施加翻转。
            if (flipX || flipY) {
                int currentX = keyframe.getRegionX();
                // 防止左移越界（若图块已在纹理最左侧，则无法修正，保留原样）
                if (currentX - regionWidth >= 0) {
                    // 1. 深拷贝（避免污染地图缓存中的原始纹理）
                    TextureRegion fixedRegion = new TextureRegion(keyframe);
                    // 2. 坐标左移一格（setRegionX 会重置 UV 为正向）
                    fixedRegion.setRegionX(currentX - regionWidth);
                    // 3. 重新施加翻转（因为 setRegionX 清除了翻转状态）
                    if (flipX) fixedRegion.flip(true, false);
                    if (flipY) fixedRegion.flip(false, true);
                    // 4. 替换当前持有的纹理
                    this.keyframe = fixedRegion;
                    // 5. 更新翻转标志为 false，因为修正后的纹理自身已经包含了翻转，
                    //    外部渲染逻辑不应该再根据这两个字段额外翻转，避免双重翻转。
                    this.flipX = false;
                    this.flipY = false;
                }
                // 若越界，则不做修正（此时 keyframe 仍为错误的右一格，但场景极少出现）
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
