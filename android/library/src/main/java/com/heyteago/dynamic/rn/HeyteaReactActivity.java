package com.heyteago.dynamic.rn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.BuildConfig;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactInstanceManagerBuilder;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.JSIModulePackage;
import com.facebook.react.bridge.JavaScriptExecutorFactory;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.devsupport.RedBoxHandler;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.uimanager.UIImplementationProvider;

import java.util.List;

public class HeyteaReactActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler, PermissionAwareActivity {
    private static final String TAG = HeyteaReactActivity.class.getName();

    protected ReactRootView mReactRootView;
    private ReactInstanceManager mReactInstanceManager;

    private HeyteaReactDelegate mHeyteaReactDelegate = createReactActivityDelegate();

    protected HeyteaReactDelegate createReactActivityDelegate() {
        return new HeyteaReactDelegate(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHeyteaReactDelegate.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHeyteaReactDelegate.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHeyteaReactDelegate.onDestroy();
        if (mReactRootView != null) {
            mReactRootView.unmountReactApplication();
            mReactRootView = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHeyteaReactDelegate.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mHeyteaReactDelegate.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mHeyteaReactDelegate.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return mHeyteaReactDelegate.onKeyLongPress(keyCode, event) || super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (!mHeyteaReactDelegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (!mHeyteaReactDelegate.onNewIntent(intent)) {
            super.onNewIntent(intent);
        }
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
        mHeyteaReactDelegate.requestPermissions(permissions, requestCode, listener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mHeyteaReactDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mHeyteaReactDelegate.onWindowFocusChanged(hasFocus);
    }

    /**
     * 默认加载App。只加载一个bundle。
     * 需要实现
     * {@link HeyteaReactActivity#getPackages()}、
     * {@link HeyteaReactActivity#getMainComponentName()}，
     * 可以选择实现 {@link HeyteaReactActivity#getJSBundleFile()} 或者 {@link HeyteaReactActivity#getBundleAssetName()}。
     * 如果 getJSBundleFile() 为空，则会获取 getBundleAssetName() 参数加载 assets 目录下的 bundle。getBundleAssetName() 的
     * 默认值为 ["index.android.bundle"]
     */
    public void loadApp() {
        mReactRootView = new ReactRootView(this);
        ReactInstanceManagerBuilder builder = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setJSMainModulePath(getJSMainModulePath())
                .setUseDeveloperSupport(getUseDeveloperSupport())
                .setRedBoxHandler(getRedBoxHandler())
                .setJavaScriptExecutorFactory(getJavaScriptExecutorFactory())
                .setUIImplementationProvider(getUIImplementationProvider())
                .setJSIModulesPackage(getJSIModulePackage())
                .setCurrentActivity(this)
                .setInitialLifecycleState(LifecycleState.BEFORE_CREATE);

        for (ReactPackage reactPackage : getPackages()) {
            builder.addPackage(reactPackage);
        }

        String jsBundleFile = getJSBundleFile();
        if (jsBundleFile != null) {
            builder.setJSBundleFile(jsBundleFile);
        } else {
            builder.setBundleAssetName(Assertions.assertNotNull(getBundleAssetName()));
        }

        mReactInstanceManager = builder.build();
        mHeyteaReactDelegate.setReactInstanceManager(mReactInstanceManager);
        mReactRootView.startReactApplication(mReactInstanceManager, getMainComponentName(), getLaunchOptions());
        setContentView(mReactRootView);

        resumeLifeCycle();
    }

    /**
     * 从端口加载一个bundle，一般在debug模式下使用，需要实现
     * {@link HeyteaReactActivity#getPackages()}
     *
     * @param address           例如 192.168.0.1
     * @param port              例如 8081
     * @param moduleName        moduleName 可在package.json 查看 name 字段
     * @param modulePath        RN 工程里 index.js 文件路径， 例如 index 或者 rn_temp/index
     * @param initialProperties 启动参数
     */
    protected void loadBundleFromServerPort(String address, int port, String moduleName, String modulePath, @Nullable Bundle initialProperties) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("debug_http_host", address + ":" + port);
        boolean isCommit = editor.commit();
        if (!isCommit) {
            Log.e(TAG, "设置地址 " + address + ":" + port + " 失败！");
            return;
        }
        mReactRootView = new ReactRootView(this);
        ReactInstanceManagerBuilder builder =
                ReactInstanceManager.builder()
                        .setApplication(getApplication())
                        .setJSMainModulePath(modulePath)
                        .setUseDeveloperSupport(getUseDeveloperSupport())
                        .setRedBoxHandler(getRedBoxHandler())
                        .setJavaScriptExecutorFactory(getJavaScriptExecutorFactory())
                        .setUIImplementationProvider(getUIImplementationProvider())
                        .setJSIModulesPackage(getJSIModulePackage())
                        .setCurrentActivity(this)
                        .setInitialLifecycleState(LifecycleState.BEFORE_CREATE);

        for (ReactPackage reactPackage : getPackages()) {
            builder.addPackage(reactPackage);
        }

        String jsBundleFile = getJSBundleFile();
        if (jsBundleFile != null) {
            builder.setJSBundleFile(jsBundleFile);
        } else {
            builder.setBundleAssetName(Assertions.assertNotNull(getBundleAssetName()));
        }
        mReactInstanceManager = builder.build();
        mHeyteaReactDelegate.setReactInstanceManager(mReactInstanceManager);
        mReactRootView.startReactApplication(mReactInstanceManager, moduleName, initialProperties);
        setContentView(mReactRootView);
    }

    /**
     * 加载bundle，asset方式。需要实现
     * {@link HeyteaReactActivity#getPackages()}
     *
     * @param moduleName        moduleName
     * @param initialProperties 启动传参
     * @param assetName         文件名
     */
    protected void loadBundleFormAsset(String moduleName, @Nullable Bundle initialProperties, String assetName) {
        mReactRootView = new ReactRootView(this);
        ReactInstanceManagerBuilder builder = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setUseDeveloperSupport(false)
                .setRedBoxHandler(getRedBoxHandler())
                .setJavaScriptExecutorFactory(getJavaScriptExecutorFactory())
                .setUIImplementationProvider(getUIImplementationProvider())
                .setJSIModulesPackage(getJSIModulePackage())
                .setCurrentActivity(this)
                .setInitialLifecycleState(LifecycleState.BEFORE_CREATE);

        for (ReactPackage reactPackage : getPackages()) {
            builder.addPackage(reactPackage);
        }
        builder.setBundleAssetName(assetName);
        mReactInstanceManager = builder.build();
        mHeyteaReactDelegate.setReactInstanceManager(mReactInstanceManager);
        mReactRootView.startReactApplication(mReactInstanceManager, moduleName, initialProperties);
        setContentView(mReactRootView);

        resumeLifeCycle();
    }

    /**
     * 加载bundle，文件方式。需要实现
     * {@link HeyteaReactActivity#getPackages()}
     *
     * @param moduleName        moduleName
     * @param initialProperties 启动传参
     * @param bundleFile        bundle文件路径
     */
    protected void loadBundleFromFile(String moduleName, @Nullable Bundle initialProperties, String bundleFile) {
        mReactRootView = new ReactRootView(this);
        ReactInstanceManagerBuilder builder =
                ReactInstanceManager.builder()
                        .setApplication(getApplication())
                        .setUseDeveloperSupport(false)
                        .setRedBoxHandler(getRedBoxHandler())
                        .setJavaScriptExecutorFactory(getJavaScriptExecutorFactory())
                        .setUIImplementationProvider(getUIImplementationProvider())
                        .setJSIModulesPackage(getJSIModulePackage())
                        .setInitialLifecycleState(LifecycleState.BEFORE_CREATE)
                        .setCurrentActivity(this);
        for (ReactPackage reactPackage : getPackages()) {
            builder.addPackage(reactPackage);
        }
        builder.setJSBundleFile(bundleFile);
        mReactInstanceManager = builder.build();
        mHeyteaReactDelegate.setReactInstanceManager(mReactInstanceManager);
        mReactRootView.startReactApplication(mReactInstanceManager, moduleName, initialProperties);
        setContentView(mReactRootView);

        resumeLifeCycle();
    }

    /**
     * 必须定义
     *
     * @return 原生模块 Packages
     */
    protected List<ReactPackage> getPackages() {
        return null;
    }

    /**
     * 只有在 {@link HeyteaReactActivity#loadApp()} 模式中有效
     *
     * @return 应用模块名称，可以在RN工程的 package.json 中查看 name 字段
     */
    protected String getMainComponentName() {
        return null;
    }

    /**
     * 只有在 {@link HeyteaReactActivity#loadApp()} 模式中有效
     *
     * @return 启动参数
     */
    protected Bundle getLaunchOptions() {
        return null;
    }

    protected boolean getUseDeveloperSupport() {
        return BuildConfig.DEBUG;
    }

    /**
     * 只有在 {@link HeyteaReactActivity#loadApp()} 、
     * {@link HeyteaReactActivity#loadBundleFromServerPort(String, int, String, String, Bundle)}
     * 模式中有效
     *
     * @return bundle文件路径，如果是分包加载，则第一个元素必须是主包，后续为业务包
     */
    protected String getJSBundleFile() {
        return null;
    }

    protected String[] getJSBundleFiles() {
        return null;
    }

    /**
     * 只有在 {@link HeyteaReactActivity#loadApp()} 、
     * {@link HeyteaReactActivity#loadBundleFromServerPort(String, int, String, String, Bundle)}
     * 模式中有效
     *
     * @return asset目录中 bundle文件的名称，如果是分包加载，则第一个元素必须是主包，后续为业务包
     */
    protected String getBundleAssetName() {
        return "index.android.bundle";
    }

    protected JSIModulePackage getJSIModulePackage() {
        return null;
    }

    protected UIImplementationProvider getUIImplementationProvider() {
        return new UIImplementationProvider();
    }

    protected JavaScriptExecutorFactory getJavaScriptExecutorFactory() {
        return null;
    }

    protected RedBoxHandler getRedBoxHandler() {
        return null;
    }

    /**
     * 只有在 {@link HeyteaReactActivity#loadApp()}， 并且是开发模式中有效
     *
     * @return RN 工程里 index.js 文件路径， 例如 index 或者 rn_temp/index
     */
    protected String getJSMainModulePath() {
        return "index";
    }

    private void resumeLifeCycle() {
        // 由于bundle是在create后，异步下载解压bundle后才加载。所以没有经历过onResume生命周期方法，这里使它来一遍
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(this, this);
        }
    }
}
