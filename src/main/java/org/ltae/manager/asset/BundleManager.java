package org.ltae.manager.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

/**
 * 多语言Bundle管理器
 */
public class BundleManager {
    private final static String TAG = BundleManager.class.getSimpleName();
    private static I18NBundle bundle;
    private static String path;
    
    public static void initialize(String path, String language) {
        BundleManager.path = path;
        AssetManager manager = AssetLoader.getManager();

        I18NBundleLoader.I18NBundleParameter bundleParameter = new I18NBundleLoader.I18NBundleParameter(Locale.of(language));
        manager.load(path, I18NBundle.class, bundleParameter);
        manager.finishLoading();
        bundle = manager.get(path, I18NBundle.class);
    }
    
    public static void changeLanguage(String language) {
        I18NBundleLoader.I18NBundleParameter bundleParameter = new I18NBundleLoader.I18NBundleParameter(Locale.of(language));
        AssetManager manager = AssetLoader.getManager();
        
        manager.unload(path);
        manager.load(path, I18NBundle.class, bundleParameter);
        manager.finishLoading();
        bundle = manager.get(path, I18NBundle.class);
    }

    public static I18NBundle getBundle() {
        if (bundle == null) {
            Gdx.app.error(TAG, "bundle is null, please call BundleManager.initialize(path, language) first!");
        }
        return bundle;
    }
}
