package com.lwh.apkdynamicloader.internal;

import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;

import dalvik.system.DexClassLoader;

/**
 * author: lanweihua
 * created on: 1/7/21 5:34 PM
 * description: 插件所需环境
 */
public class DLPluginPackage {

  private String mPackageName; //包名
  private String mDefaultActivity; //默认类名
  private DexClassLoader mDexClassLoader; //Dex加载器
  private PackageInfo mPackageInfo;
  private AssetManager mAssetManager;
  private Resources mResources;

  public DLPluginPackage(
      DexClassLoader dexClassLoader,
      PackageInfo packageInfo,
      Resources resources) {
    mResources = resources;
    mAssetManager = resources.getAssets();
    mDexClassLoader = dexClassLoader;
    mPackageInfo = packageInfo;
    mPackageName = packageInfo.packageName;
    mDefaultActivity = parseDefaultActivity(packageInfo);
  }

  private String parseDefaultActivity(PackageInfo packageInfo) {
    if (packageInfo.activities != null && packageInfo.activities.length > 0) {
      return packageInfo.activities[0].name;
    }
    return "";
  }

}
