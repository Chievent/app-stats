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

import android.content.Context;
import android.os.Build;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class FileUtils {

    private static List<File> getTopInternalCacheFolders(Context context, List<File> cacheFiles) {
        if (cacheFiles == null) {
            cacheFiles = new ArrayList<>();
        }
        // internal cache
        cacheFiles.add(context.getCacheDir());
        // code cache
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cacheFiles.add(context.getCodeCacheDir());
        }
        return cacheFiles;
    }

    private static List<File> getTopExternalCacheFolders(Context context, List<File> out) {
        if (out == null) {
            out = new ArrayList<>();
        }
        // external cache
        File file = context.getExternalCacheDir();
        if (file != null) {
            out.add(file);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                File[] files = context.getExternalCacheDirs();
                if (files != null) {
                    Collections.addAll(out, files);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    static List<File> getTopCacheFolders(Context context, List<File> out) {
        if (out == null) {
            out = new ArrayList<>();
        }

        getTopInternalCacheFolders(context, out);
        getTopExternalCacheFolders(context, out);

        return out;
    }

    private static int addAll(List<File> list, File dir, Set<String> ignoredFiles, Set<String> deletedFiles) {
        File[] files = dir.listFiles();
        int count = 0;
        if (files != null && files.length > 0) {
            for (File temp : files) {
                if (ignoredFiles != null && ignoredFiles.contains(temp.getPath())) {
                    continue;
                }

                if (deletedFiles != null && deletedFiles.contains(temp.getPath())) {
                    continue;
                }

                list.add(temp);
                count++;
            }
        }

        if (count > 0) {
            list.add(list.size() - count, dir);
            count++;
        }

        return count;
    }

    static boolean deleteFile(File file, Set<String> ignoredFiles) {
        if (file == null || !file.exists()) {
            return true;
        }

        if (file.isFile()) {
            return file.delete();
        }

        boolean result = true;
        try {
            List<File> list = new ArrayList<>();
            Set<String> deletedSet = new HashSet<>();
            int count = addAll(list, file, ignoredFiles, deletedSet);
            if (count == 0) {
                deletedSet.add(file.getPath());
            }

            while (!list.isEmpty()) {
                File temp = list.remove(list.size() - 1);
                if (ignoredFiles.contains(temp.getPath())) {
                    continue;
                } else if (temp.isFile()) {
                    result &= temp.delete();
                } else {
                    String[] paths = temp.list();
                    if (paths == null || paths.length == 0) {
                        result &= temp.delete();
                        continue;
                    }
                    count = addAll(list, temp, ignoredFiles, deletedSet);
                    if (count == 0) {
                        deletedSet.add(temp.getPath());
                    }
                }
            }
        } catch (Exception e) {
            result = false;
        }

        return result;
    }
}
