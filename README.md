# Dynamic ReactNative Android

[![maven central](https://img.shields.io/badge/maven_central-1.0.0-green)]() | [简体中文](./README-CN.md)

The solution of dynamically loading ReactNative supports default loading, asset directory loading, any file directory loading, and port debugging Metro loading.

## Download

Gradle:

```
repositories {
  google()
  jcenter()
}

dependencies {
  implementation 'coming soon...'
}
```

## ProGuard

## Guide

### Configuration

#### Ⅰ. Native project without ReactNative integration

First, generate node_modules in the same level directory of the native project and add react-native dependencies. The specific steps are as follows:

1. Execute in the same level directory of the native project folder

	```
	$ npm init
	$ npm install react-native
	```
2. Add react-native gradle related configuration to the native project

	Add in settings.gradle
	
	```
	apply from: file("../node_modules/@react-native-community/cli-platform-android/native_modules.gradle"); applyNativeModulesSettingsGradle(settings)
	```
	Add in project level build.gradle
	
	```
	allprojects {
		repositories {
			mavenLocal()
			maven {
				url("$rootDir/../node_modules/react-native/android")
			}
			maven {
				url("$rootDir/../node_modules/jsc-android/dist")
			}
			google()
			jcenter()
		}
	}
	```
	Add in app level build.gradle
	
	```
	dependencies {
		implementation 'com.facebook.react:react-native:+'
	}
	```
3. Use Android Studio to open the native project and execute Sync Now.

The final directory structure is:

TODO

#### Ⅱ. ReactNative integrated

Simply open the native project and execute Sync Now.

### Use

#### Ⅰ. Default loading

This loading method is consistent with the default loading method of React Native. If getJSBundleFile() is empty, it will get the getBundleAssetName() parameter to load the bundle under the assets directory. The default value of getBundleAssetName() is "index.android.bundle".

```java
public class MainActivity extends HeyteaReactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadApp();
    }

    @Override
    protected String getJSBundleFile() {
        return super.getJSBundleFile(); // default null
    }
    
    @Override
    protected String getBundleAssetName() {
        return super.getBundleAssetName(); // default index.android.bundle
    }
}
```

#### Ⅱ. Asset directory loading

Load the bundle file from the asset catalog. Three parameters need to be passed: the first is the moduleName, which is generally consistent with the value of the name field in the package.json of the ReactNative project; the second is the startup parameter, which can be null; the third is the file name of the bundle.

```java
public class MainActivity extends HeyteaReactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putString("key", "value");
        loadBundleFormAsset("heytea", bundle, "my_custom.bundle");
    }
}
```

#### Ⅲ. Any file directory loading

Load the bundle file from any file directory. Three parameters need to be passed: the first is moduleName, which is generally consistent with the value of the name field in the package.json of the ReactNative project; the second is the startup parameter, which can be null; the third is the file path of the bundle.

```java
public class MainActivity extends HeyteaReactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putString("key", "value");
        String bundleFile = getCacheDir() + "/bundle/index.android.bundle";
        loadBundleFromFile("heytea", bundle, bundleFile);
    }
}
```

#### Ⅳ. Port debugging Metro loading

Support the original ReactNative project to start port 8081 and use the port to debug Metro to load the bundle. Support shake to start debugging.

You need to pass 4 parameters: the first is the IP address; the second is the Metro port; the third is the moduleName, which is generally consistent with the value of the name field in the package.json of the ReactNative project; the fourth is the entry path; Five are the startup parameters, which can be null; the third is the file path of the bundle.

> Note: It needs to be in the same LAN.

```java
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
```

## Author

Heytea @qindachang

## License

MIT
