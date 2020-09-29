package com.heyteago.dynamicrn;

import android.os.Bundle;

import com.heyteago.dynamic.rn.HeyteaReactActivity;

public class MainActivity extends HeyteaReactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putString("key", "value");
        loadBundleFromServerPort("192.168.0.1", 8081, "heytea", "index", null);
//        loadBundleFromServerPort("192.168.0.1", 8081, "heytea", "rn_temp/index", bundle);
    }
}