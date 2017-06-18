/*
 * Copyright (C) 2017 Chievent (chievent@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.chievent.stats;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppStatsHelper {

    public static final long INVALID_SIZE = -1;

    public static void getStats(Context context, IPackageStatsObserver.Stub observer) {
        PackageManager pm = context.getPackageManager();
        try {
            Method getPackageSizeInfo = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            getPackageSizeInfo.invoke(pm, context.getPackageName(), observer);
            return;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            observer.onGetStatsCompleted(null, false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static String formatSize(Context context, long size) {
        return Formatter.formatFileSize(context, size);
    }

    public static boolean clearCache(Context context) {
        return clearCache(context, null);
    }

    public synchronized static boolean clearCache(Context context, Set<String> excludedFiles) {
        List<File> cacheFiles = FileUtils.getTopCacheFolders(context, null);
        try {
            for (File file : cacheFiles) {
                FileUtils.deleteFile(file, excludedFiles);
            }
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long getRamSize(Context context) {
        ActivityManager actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memoryInfo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return memoryInfo.totalMem;
        } else {
            try {
                String memTotal = "MemTotal:";
                Pattern pattern = Pattern.compile("\\d+");

                BufferedReader bufferReader = new BufferedReader(new FileReader("/proc/meminfo"));
                String line;
                while ((line = bufferReader.readLine()) != null) {
                    if (line.startsWith(memTotal)) {
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            return Long.valueOf(matcher.group()) * 1024;
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return INVALID_SIZE;
        }
    }

    public static long getRomSize(Context context) {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return statFs.getTotalBytes();
        } else {
            return (long) statFs.getBlockCount() * statFs.getBlockSize();
        }
    }

    public static long getExtStorageSize(Context context) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
            return INVALID_SIZE;
        }
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return statFs.getTotalBytes();
        } else {
            return (long) statFs.getBlockCount() * statFs.getBlockSize();
        }
    }
}
