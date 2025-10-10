package org.ltae.shader;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import org.ltae.component.ShaderComp;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 数据集：Shadertoy 标准 uniform 的实时采集与缓存。
 * 子类继承后，在 update() 或 render() 里按需把字段传进自己的 shader。
 * 本类**绝不**主动调用任何 shader.setXxx()，保持“数据层”纯净。
 */

public abstract class ShaderUniforms {
    public Entity entity;
    public ShaderProgram shaderProgram;
    //如下参数来自于https://www.shadertoy.com的规范参数,在这里将取值,子类中可以选择是否传入
    //    uniform vec3      iResolution;           // viewport resolution (in pixels)
    //    uniform float     iTime;                 // shader playback time (in seconds)
    //    uniform float     iTimeDelta;            // render time (in seconds)
    //    uniform float     iFrameRate;            // shader frame rate
    //    uniform int       iFrame;                // shader playback frame
    //    uniform float     iChannelTime[4];       // channel playback time (in seconds)
    //    uniform vec3      iChannelResolution[4]; // channel resolution (in pixels)
    //    uniform vec4      iMouse;                // mouse pixel coords. xy: current (if MLB down), zw: click
    //    uniform samplerXX iChannel0..3;          // input channel. XX = 2D/Cube
    //    uniform vec4      iDate;                 // (year, month, day, time in seconds)
    public Vector3 iResolution;
    public float iTime;
    public float iTimeDelta;
    public float iFrameRate;//帧数
    public int iFrame;//已经渲染过的帧的数量
    public Vector4 iChannelTime;
    public Vector3[] iChannelResolution;//此参数只做初始化,需要在子类实现中进行赋值与传入
    public Vector4 iMouse;
    //需要在子类中自定义
    public int iChannel0;
    public int iChannel1;
    public int iChannel2;
    public int iChannel3;
    public Vector4 iDate;

    public ShaderUniforms(Entity entity){
        this.entity = entity;
        ShaderComp shaderComp = entity.getComponent(ShaderComp.class);
        shaderProgram = shaderComp.shaderProgram;

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        int fps = Gdx.graphics.getFramesPerSecond();

        iResolution = new Vector3(width,height,Math.max(width,height));
        iTime = 0;
        iTimeDelta = 1f/fps;
        iFrameRate = fps;
        iFrame = 0;
        iChannelTime = new Vector4();
        iChannelResolution = new Vector3[4];
        iMouse = new Vector4();
        iChannel0 = 0;
        iChannel1 = 0;
        iChannel2 = 0;
        iChannel3 = 0;
        iDate = new Vector4();
    }

    /** 子类可重写，一次性初始化纹理、帧缓冲等。 */
    public void initialize(){


    };
    /** 每帧更新 uniform 数据；子类先 super.update(delta)，再按需传值。 */
    public void update(float delta){
        iTime += delta;
        iTimeDelta = delta;
        iFrame += 1;
        iChannelTime.set(iTime,iTime,iTime,iTime);
        iMouse.x = Gdx.input.getX();
        iMouse.y = Gdx.input.getY();
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            iMouse.z = iMouse.x;
            iMouse.w = iMouse.y;
        }
        LocalDateTime d = LocalDateTime.now(ZoneOffset.UTC);
        float secOfDay = d.getHour()*3600 + d.getMinute()*60 + d.getSecond() + d.getNano()*1e-9f;
        iDate.set( d.getYear(),d.getMonthValue(),d.getDayOfMonth(),secOfDay);

//        子列按需添加示例
//        shaderProgram.setUniformf("iResolution", iResolution);
//        shaderProgram.setUniformf("iTime", iTime);
//        shaderProgram.setUniformf("iTimeDelta", iTimeDelta);
//        shaderProgram.setUniformf("iFrameRate", iFrameRate);
//        shaderProgram.setUniformi("iFrame", iFrame);
//        shaderProgram.setUniformf("iMouse", iMouse);
//        shaderProgram.setUniformf("iDate", iDate);
//        shaderProgram.setUniformi("iChannel1", 1);//Texture.bind(1)
    };
}
