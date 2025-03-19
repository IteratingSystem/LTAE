package org.ltae.camera;

import com.badlogic.gdx.math.Rectangle;

/**
 * @Auther WenLong
 * @Date 2025/3/19 11:31
 * @Description 用于设定相机跟随的目标及参数
 **/
public class FollowTarget {
    public String entityTag;
    public float eCenterX = 0;
    public float eCenterY = 0;

    //此属性代表跟随中心点可以有一定的活动空间,超出此空间则跟随
    public float activeWidth = 0;
    public float activeHeight = 0;

    //平滑过渡增量
    public float progress = 0.1f;
    public FollowTarget(String entityTag){
        this.entityTag = entityTag;
    }
}
