package com.heyteago.dynamic.rn;

import android.content.Intent;
import android.os.Build;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.Callback;
import com.facebook.react.devsupport.DoubleTapReloadRecognizer;
import com.facebook.react.modules.core.PermissionListener;

public class HeyteaReactDelegate {
    private HeyteaReactActivity mActivity;
    private ReactInstanceManager mReactInstanceManager;

    private @Nullable
    PermissionListener mPermissionListener;
    private @Nullable
    Callback mPermissionsCallback;
    private DoubleTapReloadRecognizer mDoubleTapReloadRecognizer = new DoubleTapReloadRecognizer();

    public HeyteaReactDelegate(HeyteaReactActivity activity) {
        this.mActivity = activity;
    }

    public void setReactInstanceManager(ReactInstanceManager reactInstanceManager) {
        this.mReactInstanceManager = reactInstanceManager;
    }

    protected void onResume() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(mActivity, mActivity);
        }

        if (mPermissionsCallback != null) {
            mPermissionsCallback.invoke();
            mPermissionsCallback = null;
        }
    }

    protected void onPause() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostPause(mActivity);
        }
    }

    protected void onDestroy() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy(mActivity);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onActivityResult(mActivity, requestCode, resultCode, data);
        }
    }

    protected boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mReactInstanceManager != null && mActivity.getUseDeveloperSupport() && keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
            mReactInstanceManager.showDevOptionsDialog();
            return true;
        }
        return false;
    }

    protected boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mReactInstanceManager != null && mActivity.getUseDeveloperSupport()) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                mReactInstanceManager.showDevOptionsDialog();
                return true;
            }
            boolean didDoubleTapR =
                    Assertions.assertNotNull(mDoubleTapReloadRecognizer)
                            .didDoubleTapR(keyCode, mActivity.getCurrentFocus());
            if (didDoubleTapR) {
                mReactInstanceManager.getDevSupportManager().handleReloadJS();
                return true;
            }
        }
        return false;
    }

    protected boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (mReactInstanceManager != null && mActivity.getUseDeveloperSupport() && keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
            mReactInstanceManager.showDevOptionsDialog();
            return true;
        }
        return false;
    }

    protected boolean onBackPressed() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
            return true;
        }
        return false;
    }

    protected boolean onNewIntent(Intent intent) {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onNewIntent(intent);
            return true;
        }
        return false;
    }

    protected void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
        mPermissionListener = listener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mActivity.requestPermissions(permissions, requestCode);
        }
    }

    protected void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        mPermissionsCallback = new Callback() {
            @Override
            public void invoke(Object... args) {
                if (mPermissionListener != null
                        && mPermissionListener.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
                    mPermissionListener = null;
                }
            }
        };
    }

    protected void onWindowFocusChanged(boolean hasFocus) {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onWindowFocusChange(hasFocus);
        }
    }

}
