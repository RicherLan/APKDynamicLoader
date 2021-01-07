package com.lwh.apkdynamicloader.internal;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.icu.text.PluralRules;
import android.net.Uri;
import android.os.Parcelable;

/**
 * author: lanweihua
 * created on: 1/7/21 5:30 PM
 * description:
 */
public class DLIntent  extends Intent {

  private String mPluginPackage;
  private String mPluginClass;

  public DLIntent(String pluginPackage){
    mPluginPackage = pluginPackage;
  }

  public DLIntent(String pluginPackage, String pluginClass) {
    mPluginPackage = pluginPackage;
    mPluginClass = pluginClass;
  }

  public DLIntent(String pluginPackage, Class<?> clazz) {
    mPluginPackage = pluginPackage;
    mPluginClass = clazz.getName();
  }

  public Intent putExtra(String key, Parcelable value){
    return super.putExtra(key,value);
  }

  public Intent putExtra(String key, Serializable value){
    return super.putExtra(key,value);
  }

  public String getPluginPackage() {
    return mPluginPackage;
  }

  public void setPluginPackage(String pluginPackage) {
    mPluginPackage = pluginPackage;
  }

  public String getPluginClass() {
    return mPluginClass;
  }

  public void setPluginClass(String pluginClass) {
    mPluginClass = pluginClass;
  }
  public void setPluginClass(Class<?> clazz) {
    mPluginClass = clazz.getName();
  }

}
