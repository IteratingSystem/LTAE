package org.ltae.loader;



import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.bladecoder.ink.runtime.Story;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Auther WenLong
 * @Date 2025/6/30 16:31
 * @Description ink剧本加载器
 **/

public class InkStoryLoader extends AsynchronousAssetLoader<Story, InkStoryLoader.InkStoryParameter> {

    private Story story;

    public InkStoryLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, InkStoryParameter parameter) {
        return null; // 无依赖
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, InkStoryParameter parameter) {
        try {
            String json = readJson(file);
            story = new Story(json);
        } catch (Exception e) {
            throw new GdxRuntimeException("Failed to load Ink story: " + fileName, e);
        }
    }

    @Override
    public Story loadSync(AssetManager manager, String fileName, FileHandle file, InkStoryParameter parameter) {
        return story;
    }

    /** 读取文件内容为 JSON 字符串（处理 BOM 和换行） */
    private String readJson(FileHandle file) throws IOException {
        InputStream is = file.read();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            // 去除 BOM 标记
            if (sb.length() == 0) {
                line = line.replace('\uFEFF', ' ');
            }
            sb.append(line).append("\n");
        }
        br.close();
        return sb.toString();
    }

    /** 可选参数类（扩展用） */
    public static class InkStoryParameter extends AssetLoaderParameters<Story> {
        // 可添加自定义参数，如是否启用调试模式等
    }
}