package com.lwh.apkdynamicloader.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * author: lanweihua
 * created on: 1/7/21 8:03 PM
 * description: .so文件的管理
 */
public final class SoLibManager {

  private static final String TAG = SoLibManager.class.getName();
  private static final String PREFERENCE = TAG + "Preference";

  private static SoLibManager instance = new SoLibManager();

  private String mNativeLibDir;
  private ExecutorService mExecutor = Executors.newCachedThreadPool();

  public static SoLibManager getInstace() {
    return instance;
  }

  public void copySoLib(Context context, String dexPath, String nativeLibDir) {
    mNativeLibDir = nativeLibDir;
    String cpuName = CPUUtil.getCpuName();
    String cpuArchitype = CPUUtil.getCpuArchType(cpuName);

    try {
      ZipFile zipFile = new ZipFile(dexPath);
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      if (entries == null) {
        return;
      }
      while (entries.hasMoreElements()) {
        ZipEntry zipEntry = entries.nextElement();
        if (zipEntry.isDirectory()) {
          continue;
        }
        String zipName = zipEntry.getName();
        long zipLastModifytime;

        if (zipName.contains(".so") && zipName.contains(cpuArchitype)) {
          long lastModifyTime = zipEntry.getTime();
          // 相同的文件且时间也是一样的，那么不需copy
          if (lastModifyTime == getSoFileLastModifyTime(context, zipName)) {
            return;
          }
          mExecutor.submit(new CopySoLibTask(context, zipFile, zipEntry, lastModifyTime));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void setSoFileLastModifyTime(Context context, String fileName, long time) {
    SharedPreferences sh = context.getSharedPreferences(
        PREFERENCE,
        Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
    sh.edit().putLong(fileName, time);
  }

  private long getSoFileLastModifyTime(Context context, String fileName) {
    SharedPreferences sh = context.getSharedPreferences(
        PREFERENCE,
        Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
    return sh.getLong(fileName, 0);
  }

  private class CopySoLibTask implements Runnable {

    private String mSoFileName;
    private ZipFile mZipFile;
    private ZipEntry mZipEntry;
    private Context mContext;
    private long mLastModityTime;

    CopySoLibTask(Context context, ZipFile zipFile, ZipEntry zipEntry, long lastModify) {
      mZipFile = zipFile;
      mContext = context;
      mZipEntry = zipEntry;
      mSoFileName = parseSoFileName(zipEntry.getName());
      mLastModityTime = lastModify;
    }

    private final String parseSoFileName(String zipEntryName) {
      return zipEntryName.substring(zipEntryName.lastIndexOf("/") + 1);
    }

    private void writeSoFile2LibDir() throws IOException {
      InputStream is = null;
      FileOutputStream fos = null;
      is = mZipFile.getInputStream(mZipEntry);
      fos = new FileOutputStream(new File(mNativeLibDir, mSoFileName));
      copy(is, fos);
      mZipFile.close();
    }


    public void copy(InputStream is, OutputStream os) throws IOException {
      if (is == null || os == null) {
        return;
      }
      BufferedInputStream bis = new BufferedInputStream(is);
      BufferedOutputStream bos = new BufferedOutputStream(os);
      int size = getAvailableSize(bis);
      byte[] buf = new byte[size];
      int i = 0;
      while ((i = bis.read(buf, 0, size)) != -1) {
        bos.write(buf, 0, i);
      }
      bos.flush();
      bos.close();
      bis.close();
    }

    private int getAvailableSize(InputStream is) throws IOException {
      if (is == null) {
        return 0;
      }
      int available = is.available();
      return available <= 0 ? 1024 : available;
    }

    @Override
    public void run() {
      try {
        writeSoFile2LibDir();
        setSoFileLastModifyTime(mContext, mZipEntry.getName(), mLastModityTime);
      } catch (IOException e) {
        e.printStackTrace();
      }

    }

  }

}
