package com.wdy.common.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 和安装包相关的工具类
 * 安装apk
 * 卸载apk
 * 检查手机是否安装了指定的apk
 * 从apk获取版本信息
 */
public class APKUtils {
    /**
     * 安装一个apk文件
     *
     * @param uriFile
     */
    public static void install(File uriFile , Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProvider7.setIntentDataAndType(context,
                intent, "application/vnd.android.package-archive", uriFile, true);
//        intent.setDataAndType(Uri.fromFile(uriFile), "application/vnd.android.package-archive");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ResourceUtils.INSTANCE.getContext().startActivity(intent);
    }

    /**
     * 卸载一个app
     *
     * @param packageName
     */
    public static void uninstall(String packageName) {
        //通过程序的包名创建URI
        Uri packageURI = Uri.parse("package:" + packageName);
        //创建Intent意图
        Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
        //执行卸载程序
        ResourceUtils.INSTANCE.getContext().startActivity(intent);
    }

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param packageName 应用包名
     * @return
     */
    public static boolean isAvailable(String packageName) {
        // 获取packagemanager
        final PackageManager packageManager = ResourceUtils.INSTANCE.getContext().getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    /**
     * 从apk中获取版本信息
     *
     * @param channelPrefix
     * @return
     */
    public static String getChannelFromApk(String channelPrefix) {
        //从apk包中获取
        ApplicationInfo appinfo = ResourceUtils.INSTANCE.getContext().getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        //默认放在meta-inf/里， 所以需要再拼接一下
        String key = "META-INF/" + channelPrefix;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(key)) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] split = ret.split(channelPrefix);
        String channel = "";
        if (split != null && split.length >= 2) {
            channel = ret.substring(key.length());
        }
        return channel;
    }
}
