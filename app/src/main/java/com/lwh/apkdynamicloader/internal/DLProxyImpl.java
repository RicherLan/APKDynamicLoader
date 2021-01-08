package com.lwh.apkdynamicloader.internal;

import java.lang.reflect.Constructor;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.lwh.apkdynamicloader.component.DLPlugin;
import com.lwh.apkdynamicloader.utils.DLCongfigs;
import com.lwh.apkdynamicloader.utils.DLConstants;

/**
 * author: lanweihua
 * created on: 1/8/21 4:24 PM
 * description: 插件的激活与代理类的绑定
 */
public class DLProxyImpl {

  // 代理activity
  private Activity mProxyActivity;

  // 插件activity
  private DLPlugin mPluginActivity;
  private ActivityInfo mActivityInfo;

  // 要启动的类
  private String mClass;

  private DLPluginManager mDLPluginManager;
  private DLPluginPackage mDLPluginPackage;
  private AssetManager mAssetManager;
  private Resources mResources;


  public DLProxyImpl(@NonNull Activity proxyActivity) {
    mProxyActivity = proxyActivity;
  }

  public void onCreate(@NonNull Intent intent) {
    intent.setExtrasClassLoader(DLCongfigs.sClassLoader);

    String packageName = intent.getStringExtra(DLConstants.EXTRA_PACKAGE);
    String className = intent.getStringExtra(DLConstants.EXTRA_CLASS);

    mDLPluginManager = DLPluginManager.getInstance(mProxyActivity);
    mDLPluginPackage = mDLPluginManager.getPluginPackage(packageName);
    mAssetManager = mDLPluginPackage.mAssetManager;
    mResources = mDLPluginPackage.mResources;

    initializeActivityInfo();
    handleActivityInfo();
    launchTargetActivity();
  }

  private void initializeActivityInfo() {
    PackageInfo packageInfo = mDLPluginPackage.mPackageInfo;
    if ((packageInfo.activities != null) && (packageInfo.activities.length > 0)) {
      if (mClass == null) {
        mClass = packageInfo.activities[0].name;
      }

      //Finals 修复主题BUG
      int defaultTheme = packageInfo.applicationInfo.theme;
      for (ActivityInfo a : packageInfo.activities) {
        if (a.name.equals(mClass)) {
          mActivityInfo = a;
          // 修复主题没有配置的时候插件异常
          if (mActivityInfo.theme == 0) {
            if (defaultTheme != 0) {
              mActivityInfo.theme = defaultTheme;
            } else {
              if (Build.VERSION.SDK_INT >= 14) {
                mActivityInfo.theme = android.R.style.Theme_DeviceDefault;
              } else {
                mActivityInfo.theme = android.R.style.Theme;
              }
            }
          }
        }
      }
    }
  }

  private void handleActivityInfo() {

  }

  private void launchTargetActivity() {
    try {
      Class<?> localClass = getClassLoader().loadClass(mClass);
      Constructor<?> localConstructor = localClass.getConstructor(new Class[] {});
      Object instance = localConstructor.newInstance(new Object[] {});
      mPluginActivity = (DLPlugin) instance;
      ((IDLAttachInterface) mProxyActivity).attach(mPluginActivity, mDLPluginManager);
      // attach the proxy activity and plugin package to the mPluginActivity
      mPluginActivity.attach(mProxyActivity, mDLPluginPackage);

      Bundle bundle = new Bundle();
      bundle.putInt(DLConstants.FROM, DLConstants.FROM_EXTERNAL);
      mPluginActivity.onCreate(bundle);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public ClassLoader getClassLoader() {
    return mDLPluginPackage.mDexClassLoader;
  }

  public AssetManager getAssets() {
    return mAssetManager;
  }

  public Resources getResources() {
    return mResources;
  }

  public DLPlugin getRemoteActivity() {
    return mPluginActivity;
  }

}
