package org.ltae.box2d;


//传感器形状的业务类型
public enum SensorType {
    //空类型
    //交互与被交互
    //攻击与被攻击
    //脚(判断是否接触地面)
    //手(判断是否接触墙面)
    NULL,INTER,BE_INTER,ATK,BE_ATK,ON_FLOOR
}
