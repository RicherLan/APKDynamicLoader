package com.lwh.apkdynamicloader.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * author: lanweihua
 * created on: 1/7/21 8:22 PM
 * description:
 */
public class CPUUtil {

  private static final String CPU_INFO_FILE_PATH = "/proc/cpuinfo";
  // cpu指令集
  public static final String CPU_ARMEABI = "armeabi";
  public static final String CPU_X86 = "x86";
  public static final String CPU_MIPS = "mips";

  // 获得cpu name
  @Nullable
  public static String getCpuName() {
    try {
      FileReader fr = new FileReader("/proc/cpuinfo");
      BufferedReader br = new BufferedReader(fr);
      String text = br.readLine();
      br.close();
      String[] array = text.split(":\\s+", 2);
      if (array.length >= 2) {
        return array[1];
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  // 获得指令集
  @NonNull
  @SuppressLint("DefaultLocale")
  public static String getCpuArchType(String cpuName) {
    String cpuArchitect = CPU_ARMEABI;
    if (cpuName.toLowerCase().contains("arm")) {
      cpuArchitect = CPU_ARMEABI;
    } else if (cpuName.toLowerCase().contains("x86")) {
      cpuArchitect = CPU_X86;
    } else if (cpuName.toLowerCase().contains("mips")) {
      cpuArchitect = CPU_MIPS;
    }
    return cpuArchitect;
  }

}
