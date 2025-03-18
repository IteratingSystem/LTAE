package org.ltae.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import org.ltae.tiled.TileCompLoader;
import org.ltae.tiled.TileDetails;
import org.ltae.tiled.TileParam;


import java.util.Arrays;

/**
 * @Auther WenLong
 * @Date 2025/3/7 10:44
 * @Description 瓦片动画组件,大部分方法来自于libgdx的Animation是一样的,可以使用共同的文档
 **/
public class TileAnimation extends Component implements TileCompLoader {
    @TileParam
    public String name;
    @TileParam
    public String playModeName;

    protected TextureRegion[] keyFrames;
    //状态运行时间
    public float stateTime;
    //每一帧的间隔
    private float[] frameDurations;
    //随机播放模式时,每一帧持续的时间
    private float randomDuration;
    //动画的总持续事件
    private float animationDuration;
    //上一帧索引
    private int lastFrameNumber;
    //上一帧的时间戳
    private float lastStateTime;
    private Animation.PlayMode playMode;


    @Override
    public void loader(TileDetails tileDetails) {
        TiledMapTile tiledMapTile = tileDetails.tiledMapTile;
        if (!(tiledMapTile instanceof AnimatedTiledMapTile animatedTile)) {
            return;
        }

        initialize(animatedTile,playModeName);
    }

    /**
     * 用于在非组件的模式下初始化
     * 传入一个AnimatedTiledMapTile以及playModeName
     *
     * @param animatedTile
     * @param playModeName
     */
    public void initialize(AnimatedTiledMapTile animatedTile,String playModeName) {
        stateTime = 0;
        int[] animationIntervals = animatedTile.getAnimationIntervals();
        frameDurations = new float[animationIntervals.length];
        for (int i = 0; i < animationIntervals.length; i++) {
            frameDurations[i] = animationIntervals[i]/1000f;
        }

        for (float duration : frameDurations) {
            animationDuration += duration;
        }
        randomDuration = animationDuration/frameDurations.length;

        StaticTiledMapTile[] frameTiles = animatedTile.getFrameTiles();
        keyFrames = new TextureRegion[frameTiles.length];
        for (int i = 0; i < frameTiles.length; i++) {
            keyFrames[i] = frameTiles[i].getTextureRegion();
        }

        playMode = Animation.PlayMode.valueOf(playModeName);
    }


    /**
     * 判断送否是倒数第n帧之一
     * 一般用于控制动画切换时机
     * @param lastCount
     * @return
     */
    public boolean isLast(int lastCount){
        int frameNumber = this.getKeyFrameIndex(stateTime);
        int length = keyFrames.length;
        return length - frameNumber <= lastCount;
    }

    /**
     * 获取特定时刻时的帧
     *
     * @return keyFrame
     */
    public TextureRegion getKeyFrame() {
        int frameNumber = this.getKeyFrameIndex(stateTime);
        return this.keyFrames[frameNumber];
    }

    /**
     * 获取特定时刻时的帧
     *
     * @param stateTime
     * @param looping
     * @return keyFrame
     */
    private TextureRegion getKeyFrame(float stateTime, boolean looping) {
        Animation.PlayMode oldPlayMode = this.playMode;
        if (looping && (this.playMode == Animation.PlayMode.NORMAL || this.playMode == Animation.PlayMode.REVERSED)) {
            if (this.playMode == Animation.PlayMode.NORMAL) {
                this.playMode = Animation.PlayMode.LOOP;
            } else {
                this.playMode = Animation.PlayMode.LOOP_REVERSED;
            }
        } else if (!looping && this.playMode != Animation.PlayMode.NORMAL && this.playMode != Animation.PlayMode.REVERSED) {
            if (this.playMode == Animation.PlayMode.LOOP_REVERSED) {
                this.playMode = Animation.PlayMode.REVERSED;
            } else {
                this.playMode = Animation.PlayMode.LOOP;
            }
        }

        TextureRegion frame = this.getKeyFrame(stateTime);
        this.playMode = oldPlayMode;
        return frame;
    }


    /**
     * 获取特定时刻时的帧
     *
     * @param stateTime
     * @return keyFrame
     */
    private TextureRegion getKeyFrame(float stateTime) {
        int frameNumber = this.getKeyFrameIndex(stateTime);
        return this.keyFrames[frameNumber];
    }


    /**
     * 获取传入时间的帧的索引
     *
     * @param stateTime
     * @return
     */
    public int getKeyFrameIndex(float stateTime) {
        if (this.keyFrames.length == 1) {
            return 0;
        }

        int frameNumber = 0;
        //动画循环n遍,最终的余数
        float remainder = stateTime % animationDuration;
        //往返动画循环n遍,最终的余数
        float pingRemainder = stateTime % (2*animationDuration);
        float adder;

        switch (this.playMode) {
            //播放一次后停止
            case NORMAL:
                if (stateTime >= animationDuration){
                    frameNumber = keyFrames.length - 1;
                }else {
                    adder = 0;
                    for (int i = 0; i < frameDurations.length; i++) {
                        adder += frameDurations[i];
                        if (stateTime <= adder){
                            frameNumber = i;
                            break;
                        }
                    }
                }
                break;
            //循环播放
            case LOOP:
                adder = 0;
                for (int i = 0; i < frameDurations.length; i++) {
                    adder += frameDurations[i];
                    if (remainder <= adder){
                        frameNumber = i;
                        break;
                    }
                }
                break;
            //往返播放
            case LOOP_PINGPONG:
                adder = 0;
                for (int i = 0; i < frameDurations.length * 2; i++) {
                    adder += frameDurations[i];
                    if (pingRemainder <= adder){
                        frameNumber = i % frameDurations.length;
                        break;
                    }
                }
                break;
            //循环播放,随机上一张或者下一张
            case LOOP_RANDOM:
                int lastFrameNumber = (int)(this.lastStateTime / this.randomDuration);
                if (lastFrameNumber != frameNumber) {
                    frameNumber = MathUtils.random(this.keyFrames.length - 1);
                    break;
                } else {
                    frameNumber = this.lastFrameNumber;
                    break;
                }
            //反向播放一次后停止
            case REVERSED:
                if (stateTime < animationDuration){
                    adder = 0;
                    for (int i = frameDurations.length-1; i >= 0; i--) {
                        adder += frameDurations[i];
                        if (stateTime <= adder){
                            frameNumber = i;
                            break;
                        }
                    }
                }
                break;
            //反向循环播放
            case LOOP_REVERSED:
                if (stateTime < animationDuration){
                    adder = 0;
                    for (int i = frameDurations.length-1; i >= 0; i--) {
                        adder += frameDurations[i];
                        if (remainder <= adder){
                            frameNumber = i;
                            break;
                        }
                    }
                }
        }

        this.lastFrameNumber = frameNumber;
        this.lastStateTime = stateTime;
        return frameNumber;
    }


    public TextureRegion[] getKeyFrames() {
        return this.keyFrames;
    }



    /**
     * 设置帧间隔持续时间以及帧列表,所有间隔一致
     *
     * @param frameDuration
     * @param keyFrames
     */
    protected void setKeyFrames(float frameDuration,TextureRegion... keyFrames) {
        //更新帧列表
        this.keyFrames = keyFrames;
        //更新每帧持续时间
        frameDurations = new float[keyFrames.length];
        Arrays.fill(frameDurations, frameDuration);
        //更新动画总持续时间
        updateAnimationDuration();
    }

    /**
     * 设置帧间隔持续时间以及帧列表
     * 传入描述间隔时间的列表和帧列表
     *
     * @param frameDurations
     * @param keyFrames
     */
    protected void setKeyFrames(float[] frameDurations,TextureRegion... keyFrames) {
        //更新帧列表
        this.keyFrames = keyFrames;
        //更新每帧持续时间
        this.frameDurations = frameDurations;
        //更新动画总持续时间
        updateAnimationDuration();
    }


    public Animation.PlayMode getPlayMode() {
        return this.playMode;
    }


    public void setPlayMode(Animation.PlayMode playMode) {
        this.playMode = playMode;
    }


    /**
     * 返回是否完成一便的播放
     *
     * @param stateTime
     * @return
     */
    public boolean isAnimationFinished(float stateTime) {
        return stateTime >= animationDuration;
    }



    /**
     * 设置所有帧持续的时间
     * 传入一帧的时间,设置到所有帧上
     *
     * @param frameDuration
     */
    public void setFrameDuration(float frameDuration) {
        for (int i = 0; i < frameDurations.length; i++) {
            frameDurations[i] = frameDuration;
        }
        animationDuration = frameDurations.length * frameDuration;
    }

    /**
     * 设置传入帧的间隔持续时间
     * 传入帧索引以及此帧要设置的时间
     *
     * @param index
     * @param frameDuration
     */
    public void setFrameDuration(int index,float frameDuration) {
        //设置传入帧的间隔持续时间
        frameDurations[index] = frameDuration;
        //更新动画持续时间
        updateAnimationDuration();
    }

    /**
     * 获取传入索引帧的间隔持续时间
     *
     * @param index
     * @return
     */
    public float getFrameDuration(int index) {
        return frameDurations[index];
    }

    /**
     * 获取整个动画帧间隔列表
     *
     * @return
     */
    public float[] getFrameDurations() {
        return frameDurations;
    }


    public float getAnimationDuration() {
        return this.animationDuration;
    }

    private void updateAnimationDuration(){
        animationDuration = 0;
        for (float duration : frameDurations) {
            animationDuration += duration;
        }
    }
}
