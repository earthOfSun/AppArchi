package com.wdy.common.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * 作者：谢青仂 on 2016/8/15
 * 邮箱：qingle6616@sina.com
 * 1、 获取设备SD卡是否可用
 * 2、 获取设备SD卡路径
 * 3、 将视频转换成file
 * 4、 将图片转换成file
 * 5、 拼接字符串集合
 * 6、 将字符串一逗号分开成list集合
 * 7、 获取文件的路径
 * 8、 根据路径删除文件
 * 9、 根据文件删除
 * 10、 获取文件扩展名
 * 11、 根据输入流写入文件
 * 12,判断文件是否存在
 */
public class FileUtils {
    private FileUtils() {
        throw new UnsupportedOperationException("u can't fuck me...");
    }

    private static String FILE_PATH = "CESECSH/XQL/";

    /**
     * 获取设备SD卡是否可用
     */
    public static boolean isSDCardEnable() {
        return TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取设备SD卡路径
     * <p>一般是/storage/emulated/0/
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 获取图片URI
     * @param context
     * @param imageFile
     * @return
     */
    @Nullable
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 将视频转换成file
     *
     * @param context
     * @param uriString
     * @return
     */
    public static File vedioUri2File(Context context, String uriString) {
        return uri2File(context, uriString, MediaStore.Video.Media.DATA);
    }

    /**
     * 将音频转换成file
     *
     * @param context
     * @param uriString
     * @return
     */
    public static File audioUri2File(Context context, String uriString) {
        return uri2File(context, uriString, MediaStore.Audio.Media.DATA);
    }

    /**
     * 将图片转换成file
     *
     * @param context
     * @param uriString
     * @return
     */
    public static File pictureUri2File(Context context, String uriString) {
        return uri2File(context, uriString, MediaStore.Images.Media.DATA);
    }

    /**
     * 通过URI获取file对象
     * @param context
     * @param uriString
     * @param columnName
     * @return
     */
    public static File uri2File(Context context, String uriString, String columnName) {
        String[] proj = {columnName};
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://media" + uriString), proj, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(columnName);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return new File(path);
        }
        return null;
    }

    /**
     * 将bitmap图片转换成file
     *
     * @param context
     * @param bitmap
     * @return
     */
    public static File bitmap2File(Context context, Bitmap bitmap) {
        try {
            File file = new File(context.getCacheDir(), System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 拼接字符串集合
     */
    public static String spliceStr(List<String> paths) {
        if (paths != null && paths.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String path : paths) {
                stringBuilder.append(path + ",");
            }
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            return stringBuilder.toString();
        }
        return "";
    }

    /**
     * 将字符串一逗号分开成list集合
     */
    public static List<String> splitStr(String str) {
        if (!TextUtils.isEmpty(str)) {
            return Arrays.asList(str.split(","));
        }
        return null;
    }

    /**
     * 获取文件的路径
     */
    public static File getFilePath(Context context) {
        File file = null;
        if (isSDCardEnable()) {
            file = new File(getSDCardPath());
            if (!file.exists()) {
                file = getCacheDirectory(context, true);// 是否包含Android中的data部分，true包含，否则不包含
            }
        } else {
            file = getCacheDirectory(context, true);
        }
        return new File(file, FILE_PATH);
    }

    /**
     * 获取缓存目录
     * @param context
     * @param preferExternal
     * @return
     */
    public static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        } catch (IncompatibleClassChangeError e) { // (sh)it happens too (Issue #989)
            externalStorageState = "";
        }
        if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * 判断有无SD卡权限
     * @param context
     * @return
     */
    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    /**
     *获取应用程序sd卡缓存路径
     * @param context
     * @return
     */
    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
            }
        }
        return appCacheDir;
    }

    /**
     * 根据路径删除文件
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.exists() && file.delete();
    }


    /**
     * 根据文件删除
     */
    public static boolean deleteFile(File file) {
        return file != null && file.delete();
    }

    /**
     * 获取文件扩展名
     */
    public static String getExtensionName(String fileName) {
        if (fileName != null && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf(".");
            if (dot > -1 && (dot < fileName.length() - 1)) {
                return fileName.substring(dot + 1);
            }
        }
        return fileName;
    }


    /**
     * 根据输入流写入文件
     *
     * @param in
     * @param file
     */
    public static void writeFile(InputStream in, File file) throws IOException {
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        if (file != null && file.exists())
            file.delete();

        FileOutputStream out = new FileOutputStream(file);
        byte[] buffer = new byte[1024 * 128];
        int len = -1;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        out.flush();
        out.close();
        in.close();

    }

    /**
     * 判断目录是否存在,不存在就创建
     *
     * @param path
     */
    public static boolean checkDir(String path) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        return true;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param filePath       指定目录
     * @param deleteThisPath 是否删除这个目录
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 如果下面还有文件
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }

                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
