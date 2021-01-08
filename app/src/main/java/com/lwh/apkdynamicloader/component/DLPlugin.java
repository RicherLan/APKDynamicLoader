package com.lwh.apkdynamicloader.component;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.lwh.apkdynamicloader.internal.DLPluginPackage;

/**
 * author: lanweihua
 * created on: 1/8/21 10:52 AM
 * description: 内外部通信接口
 */
public interface DLPlugin {

  public void attach(Activity proxyActivity, DLPluginPackage pluginPackage);

  public void onCreate(Bundle savedInstanceState);

  public void onStart();

  public void onRestart();

  public void onActivityResult(int requestCode, int resultCode, Intent data);

  public void onResume();

  public void onPause();

  public void onStop();

  public void onDestroy();

  public void onSaveInstanceState(Bundle outState);

  public void onNewIntent(Intent intent);

  public void onRestoreInstanceState(Bundle savedInstanceState);

  public boolean onTouchEvent(MotionEvent event);

  public boolean onKeyUp(int keyCode, KeyEvent event);

  public void onWindowAttributesChanged(WindowManager.LayoutParams params);

  public void onWindowFocusChanged(boolean hasFocus);

  public void onBackPressed();

}
