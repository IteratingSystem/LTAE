package org.ltae.box2d;

/**
 * @Auther WenLong
 * @Date 2025/2/18 11:03
 * @Description keyframe动画帧的形状夹具中可以传入此对象用于描述一些业务信息
 **/
public class KeyframeShapeData extends DefFixData{
    public String aniName;
    public int keyframeIndex;
    public boolean isFlipX = false;
    //是否处理,用于一些判断中,只能处理一次,虽然会有很多帧
    public boolean isProcessed = false;
}
