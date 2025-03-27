package org.ltae.b2d;

/**
 * @Auther WenLong
 * @Date 2025/3/27 17:15
 * @Description 动画帧形状数据
 **/
public class KeyframeFixData extends DefFixData {
    //已经激活过了
    public boolean isAfter = false;
    //所在帧
    public int keyframeIndex;
    //所在动画名称
    public String aniName;
}
