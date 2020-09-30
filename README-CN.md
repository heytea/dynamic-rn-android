# Dynamic ReactNative Android

[![maven central](https://img.shields.io/badge/maven_central-1.0.0-green)]()

动态加载ReactNative的方案，支持[默认加载](#ⅰ-默认加载)、[Asset目录加载](#ⅱ-asset目录加载)、[任意文件目录加载](#ⅲ-任意文件目录加载)、[端口调试Metro加载](#ⅳ-端口调试metro加载)。

## Download

Gradle:

```
repositories {
  google()
  jcenter()
}

dependencies {
  implementation 'com.heyteago:dynamicrn:1.0.0'
}
```

## ProGuard


## Guide

### 工程配置

#### Ⅰ. 未集成ReactNative的原生工程

首先在原生工程的同级目录下，生成 node_modules，添加 react-native 依赖，具体步骤如下：

1. 在原生工程文件夹的同级目录下执行

	```shell
	$ npm init
	$ npm install react-native
	```
2. 原生工程中添加 react-native gradle 相关配置

	settings.gradle 中添加
	
	```gradle
	apply from: file("../node_modules/@react-native-community/cli-platform-android/native_modules.gradle"); applyNativeModulesSettingsGradle(settings)
	```
	项目级别 build.gradle 中添加
	
	```gradle
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
            maven { url 'https://www.jitpack.io' }
        }
    }
	```
	app 级别 build.gradle 中添加
	
	```gradle
	dependencies {
		implementation 'com.facebook.react:react-native:+'
	}
    apply from: file("../../node_modules/@react-native-community/cli-platform-android/native_modules.gradle"); applyNativeModulesAppBuildGradle(project)
	```
3. 使用 Android Studio 打开原生工程，执行 Sync Now。

最终的目录结构为：



#### Ⅱ. 已集成ReactNative

直接打开原生工程执行 Sync Now 即可。

### 使用

#### Ⅰ. 默认加载

这种加载方式与 React Native 默认的加载方式保持一致，如果 getJSBundleFile() 为空，则会获取 getBundleAssetName() 参数加载 assets 目录下的 bundle。getBundleAssetName() 默认值为 "index.android.bundle"。

```java
public class MainActivity extends HeyteaReactActivity {

    @Override
    protected List<ReactPackage> getPackages() {
        @SuppressWarnings("UnnecessaryLocalVariable")
        List<ReactPackage> packages = new PackageList(this.getApplication()).getPackages();
        // Packages that cannot be autolinked yet can be added manually here, for example:
        // packages.add(new MyReactNativePackage());
        return packages;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadApp(); // 默认加载
    }

    @Override
    protected String getJSBundleFile() {
        return super.getJSBundleFile(); // 默认值为 null
    }
    
    @Override
    protected String getBundleAssetName() {
        return super.getBundleAssetName(); // 默认值为 index.android.bundle
    }
}
```

> 注意：PackageList 为编译生成的，你可能需要先运行 Build -> Make Project 才能导入这个文件。

#### Ⅱ. Asset目录加载

从 asset 目录中加载 bundle 文件。需要传递3个参数：第一个为moduleName，一般与 ReactNative 工程的 package.json 中 name 字段的值保持一致；第二个为启动传参，可以为空；第三个为 bundle 的文件名。

```java
public class MainActivity extends HeyteaReactActivity {

    @Override
    protected List<ReactPackage> getPackages() {
        @SuppressWarnings("UnnecessaryLocalVariable")
        List<ReactPackage> packages = new PackageList(this.getApplication()).getPackages();
        // Packages that cannot be autolinked yet can be added manually here, for example:
        // packages.add(new MyReactNativePackage());
        return packages;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putString("key", "value");
        loadBundleFormAsset("heytea", bundle, "my_custom.bundle"); // Asset目录加载
    }
}
```

#### Ⅲ. 任意文件目录加载

从任意文件目录加载 bundle 文件。需要传递3个参数：第一个为moduleName，一般与 ReactNative 工程的 package.json 中 name 字段的值保持一致；第二个为启动传参，可以为空；第三个为 bundle 的文件路径。

```java
public class MainActivity extends HeyteaReactActivity {

    @Override
    protected List<ReactPackage> getPackages() {
        @SuppressWarnings("UnnecessaryLocalVariable")
        List<ReactPackage> packages = new PackageList(this.getApplication()).getPackages();
        // Packages that cannot be autolinked yet can be added manually here, for example:
        // packages.add(new MyReactNativePackage());
        return packages;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putString("key", "value");
        String bundleFile = getCacheDir() + "/bundle/index.android.bundle";
        loadBundleFromFile("heytea", bundle, bundleFile); // 任意文件目录加载
    }
}
```

#### Ⅳ. 端口调试Metro加载

支持原有的 ReactNative 工程启动 8081 端口，使用端口调试 Metro 加载bundle。支持摇一摇开启调试。

需要传递4个参数：第一个为IP地址；第二个为 Metro 端口；第三个为moduleName，一般与 ReactNative 工程的 package.json 中 name 字段的值保持一致；第四个为入口路径；第五个为启动传参，可以为空。

> 注：需要在同一个局域网内。

```java
public class MainActivity extends HeyteaReactActivity {

    @Override
    protected List<ReactPackage> getPackages() {
        @SuppressWarnings("UnnecessaryLocalVariable")
        List<ReactPackage> packages = new PackageList(this.getApplication()).getPackages();
        // Packages that cannot be autolinked yet can be added manually here, for example:
        // packages.add(new MyReactNativePackage());
        return packages;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putString("key", "value");
        loadBundleFromServerPort("192.168.0.1", 8081, "heytea", "index", null); // 端口调试Metro加载
//        loadBundleFromServerPort("192.168.0.1", 8081, "heytea", "rn_temp/index", bundle);
    }
}
```


## Author

Heytea @qindachang

## License

Apache-2.0
