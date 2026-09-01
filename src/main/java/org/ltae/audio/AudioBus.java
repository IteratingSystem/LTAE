package org.ltae.audio;

/**
 * 音频总线。游戏可以分别控制不同类别声音的音量与播放状态。
 */
public enum AudioBus {
    SFX,
    UI,
    AMBIENT,
    MUSIC
}
