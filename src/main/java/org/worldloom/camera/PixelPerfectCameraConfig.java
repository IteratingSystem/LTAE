package org.worldloom.camera;

/**
 * 平滑像素摄像机配置。
 *
 * <p>世界先使用像素对齐后的摄像机绘制到扩边缓冲，再以逻辑摄像机丢失的
 * 小数位移整体补偿到屏幕。这样可以保持最近邻采样，同时避免各对象边缘
 * 在摄像机移动时分别跳动。</p>
 */
public final class PixelPerfectCameraConfig {
    private final boolean enabled;
    private final int overscanPixels;

    private PixelPerfectCameraConfig(boolean enabled, int overscanPixels) {
        if (overscanPixels < 1) {
            throw new IllegalArgumentException(
                "overscanPixels must be at least one");
        }
        this.enabled = enabled;
        this.overscanPixels = overscanPixels;
    }

    /** 创建关闭状态的配置。 */
    public static PixelPerfectCameraConfig disabled() {
        return new PixelPerfectCameraConfig(false, 1);
    }

    /** 使用一像素扩边启用平滑像素摄像机。 */
    public static PixelPerfectCameraConfig enabled() {
        return new PixelPerfectCameraConfig(true, 1);
    }

    /** 使用指定扩边像素数启用平滑像素摄像机。 */
    public static PixelPerfectCameraConfig enabled(int overscanPixels) {
        return new PixelPerfectCameraConfig(true, overscanPixels);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getOverscanPixels() {
        return overscanPixels;
    }
}
