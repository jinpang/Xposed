package com.jp.xposedtest.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.jp.xposedtest.MainActivity;
import com.jp.xposedtest.XposedReceiver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.RandomAccessFile;

import de.robv.android.xposed.XposedBridge;

/**
 * @projectName: XposedTest
 * @package: com.jp.xposedtest.utils
 * @className: JSoupUtil
 * @description: java类作用描述
 * @author: pangjingzhong
 * @email: jingzhongp@gmail.com
 * @createDate: 2019/7/24 15:54
 * @updateUser: 更新者
 * @updateDate: 2019/7/24 15:54
 * @updateRemark: 更新说明
 * @version: 1.0
 * @copyright: 2018-2019 (C)深圳市冰禾网络科技有限公司 Inc. All rights reserved.
 */
public class JSoupUtil {

    public static void parseUrl(final Context context, final String url) {
        XposedBridge.log("parseUrl:" + url);
        if (!TextUtils.isEmpty(url)) {
            String tmpUrl = url;
            String prefixHttp = "http://";
            String prefixHttps = "https://";
            if (url.contains(prefixHttp) || url.contains(prefixHttps)) {
                if (url.contains(prefixHttp)) {
                    tmpUrl = url.substring(url.indexOf(prefixHttp));
                } else if (url.contains(prefixHttps)) {
                    tmpUrl = url.substring(url.indexOf(prefixHttps));
                }
            }
            final String url2 = tmpUrl;
            if (URLUtil.isHttpUrl(url2) || URLUtil.isHttpsUrl(url2)) {
                XposedBridge.log("parseUrl in:" + url);
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Uri uri = Uri.parse(url2);
                                    String type = uri.getQueryParameter("q");
                                    Document doc = Jsoup.connect(url2).get();
                                    String title = doc.title();
                                    XposedBridge.log("parseUrl title:" + type + "=" + title);
                                    String name = type;
                                    if (TextUtils.isEmpty(name)) {
                                        name = title;
                                    }
                                    if (TextUtils.isEmpty(name)) {
                                        name = "uc";
                                    }
                                    String parentPath = PropertyUtil.readValue(MainActivity.CONFIG_PATH, "path", "/storage/emulated/legacy");
                                    writeTxtToFile(context, doc.html(), parentPath, "123.txt");
                                    //搜索页进来的
                                /*ArrayList<String> texts = new ArrayList<>();
                                if (!TextUtils.isEmpty(type)){
                                    Elements eles = doc.select("a");
                                    if (eles != null){
                                        for (Element el: eles) {
                                            String href = el.select("a").attr("href");
                                            if (el.hasText()) {
                                                String t = el.text();
                                                texts.add(t + "=" + href);
                                                XposedBridge.log("parseUrl searcha1:" + t + "=" + href);
                                            }
                                        }
                                        XposedBridge.log("parseUrl search a size:" + texts.size() + ":" + texts.toString());
                                        XposedBridge.log("parseUrl searcha3:" + eles.toString());
                                    }
                                    writeTxtToFile(context, texts, Environment.getExternalStorageDirectory().getAbsolutePath(), type + ".txt");
                                }*/
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).start();
            }
        }
    }

    // 将字符串写入到文本文件中
    private static void writeTxtToFile(Context context, String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);
        String strFilePath;
        if (filePath.endsWith("/")) {
            strFilePath = filePath + fileName;
        } else {
            strFilePath = filePath + "/" + fileName;
        }
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        File file = null;
        try {
            file = new File(strFilePath);
            if (!file.exists()) {
                XposedBridge.log("parseUrl" + " Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            ///raf.seek(file.length());直接覆盖存在文件
            raf.write(strContent.getBytes());
            raf.close();
            XposedBridge.log("parseUrl" + " save the file:" + strFilePath);
            if (context != null) {
                Util.sendBroadcast(context, XposedReceiver.ACTION_SEARCH_OK, "search", strFilePath);
            }
        } catch (Exception e) {
            XposedBridge.log("parseUrl" + " Error on write File:" + e);
            //如果报错，则该文件删除
            if (file != null) {
                file.deleteOnExit();
            }
            if (context != null) {
                Util.sendBroadcast(context, XposedReceiver.ACTION_SEARCH_FAILED, "search", e.getMessage());
            }
        }
    }

    //生成文件
    private static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            if (filePath.endsWith("/")) {
                file = new File(filePath + fileName);
            } else {
                file = new File(filePath + "/" + fileName);
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

//生成文件夹

    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            XposedBridge.log("parseUrl error:" + e + "");
        }
    }
}
