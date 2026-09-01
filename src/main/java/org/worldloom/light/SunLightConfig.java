package org.worldloom.light;

/**
 * 俯视角太阳的昼夜轨迹配置。
 */
public final class SunLightConfig {
    private static final float DEFAULT_MINIMUM_ELEVATION_DEGREE = 26.56505f;
    private static final float DEFAULT_MAXIMUM_ELEVATION_DEGREE = 51.34019f;

    private float referenceHour = 6f;
    private float referenceBearingDegree = 0f;
    private float dailyBearingSweepDegree = -360f;
    private float minimumElevationDegree =
        DEFAULT_MINIMUM_ELEVATION_DEGREE;
    private float maximumElevationDegree =
        DEFAULT_MAXIMUM_ELEVATION_DEGREE;

    public float getReferenceHour() {
        return referenceHour;
    }

    public float getReferenceBearingDegree() {
        return referenceBearingDegree;
    }

    public SunLightConfig setReference(float referenceHour,
                                       float referenceBearingDegree) {
        if (referenceHour < 0f || referenceHour >= 24f) {
            throw new IllegalArgumentException(
                "referenceHour must be from zero up to but not including 24");
        }
        this.referenceHour = referenceHour;
        this.referenceBearingDegree = referenceBearingDegree;
        return this;
    }

    public float getDailyBearingSweepDegree() {
        return dailyBearingSweepDegree;
    }

    public SunLightConfig setDailyBearingSweepDegree(
        float dailyBearingSweepDegree) {
        this.dailyBearingSweepDegree = dailyBearingSweepDegree;
        return this;
    }

    public float getMinimumElevationDegree() {
        return minimumElevationDegree;
    }

    public float getMaximumElevationDegree() {
        return maximumElevationDegree;
    }

    public SunLightConfig setElevationRange(
        float minimumElevationDegree, float maximumElevationDegree) {
        if (minimumElevationDegree <= 0f || maximumElevationDegree >= 90f
            || minimumElevationDegree > maximumElevationDegree) {
            throw new IllegalArgumentException(
                "sun elevation must stay between zero and 90 degrees");
        }
        this.minimumElevationDegree = minimumElevationDegree;
        this.maximumElevationDegree = maximumElevationDegree;
        return this;
    }
}
