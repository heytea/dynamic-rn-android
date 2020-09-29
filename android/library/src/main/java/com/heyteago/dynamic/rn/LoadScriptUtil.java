package com.heyteago.dynamic.rn;

import android.content.Context;

import androidx.annotation.NonNull;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.ReactContext;

public class LoadScriptUtil {
    private LoadScriptUtil() {}
    public static void loadScriptFromAsset(ReactInstanceManager reactInstanceManager,
                                           boolean loadSynchronously,
                                           @NonNull String... bundleNames) {
        CatalystInstance catalystInstance = getCatalystInstance(reactInstanceManager);
        if (catalystInstance == null) {
            return;
        }
        Context context = reactInstanceManager.getCurrentReactContext();
        if (context == null) {
            return;
        }
        String[] sources = new String[bundleNames.length];
        for (int i = 0; i < bundleNames.length; i += 1) {
            sources[i] = "assets://" + bundleNames[i];
            catalystInstance.loadScriptFromAssets(context.getApplicationContext().getAssets(), sources[i], loadSynchronously);
        }
    }

    public static void loadScriptFromFile(ReactInstanceManager reactInstanceManager,
                                          boolean loadSynchronously,
                                          @NonNull String... bundleFiles) {
        CatalystInstance catalystInstance = getCatalystInstance(reactInstanceManager);
        if (catalystInstance == null) {
            return;
        }
        Context context = reactInstanceManager.getCurrentReactContext();
        if (context == null) {
            return;
        }
        for (String bundleFile : bundleFiles) {
            catalystInstance.loadScriptFromFile(bundleFile, bundleFile, loadSynchronously);
        }
    }

    public static CatalystInstance getCatalystInstance(ReactInstanceManager reactInstanceManager) {
        ReactContext reactContext = reactInstanceManager.getCurrentReactContext();
        if (reactContext == null) {
            return null;
        }
        return reactContext.getCatalystInstance();
    }
}
