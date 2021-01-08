package com.lwh.apkdynamicloader.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;

import com.lwh.apkdynamicloader.internal.DLIntent;
import com.lwh.apkdynamicloader.internal.DLPluginManager;
import com.lwh.apkdynamicloader.internal.DLPluginPackage;
import com.lwh.apkdynamicloader.utils.DLConstants;

/**
 * author: lanweihua
 * created on: 1/8/21 11:04 AM
 * description:
 */
public class DLBasePluginActivity extends Activity implements DLPLugin {

  private static final String TAG = "DLBasePluginActivity";

  // 代理类
  protected Activity mProxyActivity;
  // 用来替换this: 如果是插件内部自己打开的话，那么等于this，否则指向的是代理类；
  protected Activity that;
  protected DLPluginManager mDLPluginManager;
  protected DLPluginPackage mDLPluginPackage;

  protected int mFrom = DLConstants.FROM_INTERNAL;

  @Override
  public void attach(@NonNull Activity proxyActivity, @NonNull DLPluginPackage pluginPackage) {
    Log.d(TAG, "attach: proxyActivity= " + proxyActivity);
    mProxyActivity = proxyActivity;
    that = mProxyActivity;
    mDLPluginPackage = pluginPackage;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    if(savedInstanceState!=null){
      mFrom = savedInstanceState.getInt(DLConstants.FROM);
    }
    if(isFromInternal()){
      super.onCreate(savedInstanceState);
      mProxyActivity = this;
      that = this;
    }
    mDLPluginManager = DLPluginManager.getInstance(that);
    Log.d(TAG, "onCreate: from= "
        + (mFrom == DLConstants.FROM_INTERNAL ? "DLConstants.FROM_INTERNAL" : "FROM_EXTERNAL"));
  }

  @Override
  public void setContentView(View view) {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.setContentView(view);
    } else {
      mProxyActivity.setContentView(view);
    }
  }

  @Override
  public void setContentView(View view, ViewGroup.LayoutParams params) {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.setContentView(view, params);
    } else {
      mProxyActivity.setContentView(view, params);
    }
  }

  @Override
  public void setContentView(int layoutResID) {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.setContentView(layoutResID);
    } else {
      mProxyActivity.setContentView(layoutResID);
    }
  }

  @Override
  public void addContentView(View view, ViewGroup.LayoutParams params) {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.addContentView(view, params);
    } else {
      mProxyActivity.addContentView(view, params);
    }
  }

  @Override
  public View findViewById(int id) {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      return super.findViewById(id);
    } else {
      return mProxyActivity.findViewById(id);
    }
  }

  @Override
  public Intent getIntent() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      return super.getIntent();
    } else {
      return mProxyActivity.getIntent();
    }
  }

  @Override
  public ClassLoader getClassLoader() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      return super.getClassLoader();
    } else {
      return mProxyActivity.getClassLoader();
    }
  }

  @Override
  public Resources getResources() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      return super.getResources();
    } else {
      return mProxyActivity.getResources();
    }
  }

  @Override
  public String getPackageName() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      return super.getPackageName();
    } else {
      return mDLPluginPackage.mPackageName;
    }
  }

  @Override
  public LayoutInflater getLayoutInflater() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      return super.getLayoutInflater();
    } else {
      return mProxyActivity.getLayoutInflater();
    }
  }

  @Override
  public MenuInflater getMenuInflater() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      return super.getMenuInflater();
    } else {
      return mProxyActivity.getMenuInflater();
    }
  }

  @Override
  public SharedPreferences getSharedPreferences(String name, int mode) {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      return super.getSharedPreferences(name, mode);
    } else {
      return mProxyActivity.getSharedPreferences(name, mode);
    }
  }

  @Override
  public Context getApplicationContext() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      return super.getApplicationContext();
    } else {
      return mProxyActivity.getApplicationContext();
    }
  }

  @Override
  public WindowManager getWindowManager() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      return super.getWindowManager();
    } else {
      return mProxyActivity.getWindowManager();
    }
  }

  @Override
  public Window getWindow() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      return super.getWindow();
    } else {
      return mProxyActivity.getWindow();
    }
  }

  @Override
  public Object getSystemService(String name) {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      return super.getSystemService(name);
    } else {
      return mProxyActivity.getSystemService(name);
    }
  }

  @Override
  public void finish() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.finish();
    } else {
      mProxyActivity.finish();
    }
  }

  @Override
  public void onBackPressed() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.onBackPressed();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  public void onStart() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.onStart();
    }
  }

  @Override
  public void onRestart() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.onRestart();
    }
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.onRestoreInstanceState(savedInstanceState);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.onSaveInstanceState(outState);
    }
  }

  public void onNewIntent(Intent intent) {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.onNewIntent(intent);
    }
  }

  @Override
  public void onResume() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.onResume();
    }
  }

  @Override
  public void onPause() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.onPause();
    }
  }

  @Override
  public void onStop() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.onStop();
    }
  }

  @Override
  public void onDestroy() {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.onDestroy();
    }
  }

  public boolean onTouchEvent(MotionEvent event) {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      return super.onTouchEvent(event);
    }
    return false;
  }

  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      return super.onKeyUp(keyCode, event);
    }
    return false;
  }

  public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.onWindowAttributesChanged(params);
    }
  }

  public void onWindowFocusChanged(boolean hasFocus) {
    if (mFrom == DLConstants.FROM_INTERNAL) {
      super.onWindowFocusChanged(hasFocus);
    }
  }

  public int startPluginActivity(DLIntent dlIntent) {
    return startPluginActivityForResult(dlIntent, -1);
  }

  public int startPluginActivityForResult(DLIntent dlIntent, int requestCode) {
    if (mFrom == DLConstants.FROM_EXTERNAL) {
      if (dlIntent.getPluginPackage() == null) {
        dlIntent.setPluginPackage(mDLPluginPackage.mPackageName);
      }
    }
    return mDLPluginManager.startPluginActivityForResult(that, dlIntent, requestCode);
  }

  // 是否是插件内部调用的
  private boolean isFromInternal(){
    return mFrom == DLConstants.FROM_INTERNAL;
  }

}
