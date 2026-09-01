package org.worldloom.audio;

/**
 * 统一定义音频资源目录，并把游戏侧的简短名称转换为完整资源路径。
 */
public final class AudioAssetPath {
    public static final String SOUND_DIRECTORY = "audio/sounds/";
    public static final String MUSIC_DIRECTORY = "audio/music/";
    private static final String DEFAULT_EXTENSION = ".ogg";

    private AudioAssetPath() {
    }

    public static String sound(String path) {
        return resolve(path, SOUND_DIRECTORY);
    }

    public static String music(String path) {
        return resolve(path, MUSIC_DIRECTORY);
    }

    private static String resolve(String path, String directory) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("audio path cannot be blank");
        }
        String normalized = path.trim().replace('\\', '/');
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (!normalized.startsWith(directory)) {
            if (normalized.startsWith("audio/")) {
                throw new IllegalArgumentException(
                    "audio path does not match playback type: " + path);
            }
            normalized = directory + normalized;
        }
        if (!hasAudioExtension(normalized)) {
            normalized += DEFAULT_EXTENSION;
        }
        return normalized;
    }

    private static boolean hasAudioExtension(String path) {
        String lowerPath = path.toLowerCase();
        return lowerPath.endsWith(".ogg") || lowerPath.endsWith(".wav")
            || lowerPath.endsWith(".mp3");
    }
}
