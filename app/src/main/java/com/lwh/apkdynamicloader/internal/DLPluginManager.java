package com.lwh.apkdynamicloader.internal;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lwh.apkdynamicloader.utils.DLConstants;
import com.lwh.apkdynamicloader.utils.SoLibManager;

import dalvik.system.DexClassLoader;

/**
 * author: lanweihua
 * created on: 1/7/21 6:21 PM
 * description:
 */
public class DLPluginManager {

  public static final String TAG = "DLPluginManager";

  private static final int START_RESULT_SUCCESS = 0;
  // package不存在
  private static final int START_RESULT_NO_PACKAGE = 1;
  // class不存在
  private static final int START_RESULT_NO_CLASS = 2;
  // 类型错误
  private static final int START_RESULT_TYPE_ERROR = 3;

  private static DLPluginManager sInstance;

  @NonNull
  private Context mContext;
  private HashMap<String, DLPluginPackage> mPluginPackagesHolder = new HashMap<>();

  private String mNativeLibDir = null;

  // 调用方是插件内部还是外部
  private int mFrom = DLConstants.FROM_INTERNAL;

  public static DLPluginManager getInstance(Context context) {
    return sInstance == null
        ? InstanceGenerator.getManagerInstance(context)
        : sInstance;
  }

  private DLPluginManager(@NonNull Context context) {
    mContext = context;
    mNativeLibDir = mContext.getDir("pluginlib", Context.MODE_PRIVATE).getAbsolutePath();
  }

  /**
   * 加载apk
   *
   * @param dexPath 路径
   * @return
   */
  public DLPluginPackage loadAPK(String dexPath) {
    return loadAPK(dexPath, true);
  }

  public DLPluginPackage loadAPK(String dexPath, boolean hasSoLib) {
    mFrom = DLConstants.FROM_EXTERNAL;
    PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(
        dexPath,
        PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
    if (packageInfo == null) {
      return null;
    }
    DLPluginPackage pluginPackage = preparePluginEnv(packageInfo, dexPath);
    if (hasSoLib) {
      copySoLib(dexPath);
    }
    return pluginPackage;
  }

  @NonNull
  private DLPluginPackage preparePluginEnv(PackageInfo packageInfo, String dexpath) {
    DLPluginPackage pluginPackage = mPluginPackagesHolder.get(packageInfo.packageName);
    if (pluginPackage != null) {
      return pluginPackage;
    }
    DexClassLoader dexClassLoader = createDexClassLoader(dexpath);
    AssetManager assetManager = createAssetmanager(dexpath);
    Resources resources = createResources(assetManager);
    pluginPackage = new DLPluginPackage(dexClassLoader, packageInfo, resources);
    mPluginPackagesHolder.put(packageInfo.packageName, pluginPackage);
    return pluginPackage;
  }

  // 构建dex类加载器
  @NonNull
  private DexClassLoader createDexClassLoader(String dexPath) {
    File dexOutputDir = mContext.getDir("dex", Context.MODE_PRIVATE);
    String dexOutputDirPath = dexOutputDir.getAbsolutePath();
    return new DexClassLoader(
        dexPath,
        dexOutputDirPath,
        null,
        mContext.getClassLoader());
  }

  // 构建assetManager
  @Nullable
  private AssetManager createAssetmanager(String dexPath) {
    AssetManager assetManager = null;
    try {
      assetManager = AssetManager.class.newInstance();
      Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
      addAssetPath.invoke(assetManager, dexPath);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return assetManager;
  }

  @NonNull
  private Resources createResources(AssetManager assetManager) {
    Resources superResources = mContext.getResources();
    return new Resources(
        assetManager,
        superResources.getDisplayMetrics(),
        superResources.getConfiguration());
  }

  // 拷贝.so文件到
  private void copySoLib(String dexPath) {
    SoLibManager.getInstace().copySoLib(mContext, dexPath, mNativeLibDir);
  }

  private static class InstanceGenerator {
    public static DLPluginManager getManagerInstance(Context context) {
      return new DLPluginManager(context);
    }
  }

}
