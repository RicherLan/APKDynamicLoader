package com.lwh.apkdynamicloader.internal;

import com.lwh.apkdynamicloader.component.DLPlugin;

/**
 * author: lanweihua
 * created on: 1/8/21 4:13 PM
 * description: 绑定插件activity
 */
public interface IDLAttachInterface {

  void attach(DLPlugin dlpLugin, DLPluginManager dlPluginManager);

}
