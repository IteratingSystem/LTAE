package org.ltae.box2d;

import com.artemis.Entity;
import org.ltae.box2d.listener.EcsContactListener;


/**
 * @Auther WenLong
 * @Date 2025/2/18 11:03
 * @Description keyframe动画帧的形状夹具中可以传入此对象用于描述一些业务信息
 **/
public class KeyframeShapeData extends DefFixData{
    public String aniName;
    public int keyframeIndex;
}
