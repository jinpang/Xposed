package com.jp.xposedtest.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.jp.xposedtest.MainActivity;
import com.jp.xposedtest.XposedReceiver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
                                doJsoup(url2, context);
                            }
                        }
                ).start();
            }
        }
    }

    private static synchronized void doJsoup(final String url2, final Context context) {
        try {
            Uri uri = Uri.parse(url2);
            String type = uri.getQueryParameter("q");
            Document doc = Jsoup.connect(url2).get();
            String title = doc.title();
            XposedBridge.log("parseUrl title:" + type + "=" + title);
            /*String name = type;
            if (TextUtils.isEmpty(name)) {
                name = title;
            }
            if (TextUtils.isEmpty(name)) {
                name = "uc";
            }*/
            String parentPath = PropertyUtil.readValue(MainActivity.CONFIG_PATH, "path", "/storage/emulated/legacy");
            StringBuffer buffer = new StringBuffer();
            buffer.append("标题:");
            buffer.append(title);
            buffer.append("\n");
            buffer.append("网址:");
            buffer.append(url2);
            buffer.append("\n");
            String docHtml = doc.html();
            buffer.append("文件大小：");
            buffer.append(docHtml.length());
            buffer.append("\n");
            buffer.append("下面为网页全部内容：\n");
            buffer.append("########################## BEGIN ##########################\n\n");
            buffer.append(docHtml);
            buffer.append("\n\n########################## END ##########################\n\n");
            writeTxtToFile(context, buffer.toString(), parentPath, "123.txt", title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 将字符串写入到文本文件中
    private static void writeTxtToFile(Context context, String strcontent, String filePath, String fileName, String title) {
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
        FileWriter fwriter = null;
        try {
            file = new File(strFilePath);
            if (!file.exists()) {
                XposedBridge.log("parseUrl" + " Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            //直接清空覆盖文件内容
            fwriter = new FileWriter(file);
            fwriter.write(strContent);
//只是覆盖文件内容，并没有清空处理
//            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
//            ///raf.seek(file.length());直接覆盖存在文件
//            raf.write(strContent.getBytes());
//            raf.close();
            XposedBridge.log("parseUrl" + " save the file:" + strFilePath);
            if (context != null) {
                Util.sendBroadcast(context, XposedReceiver.ACTION_SEARCH_OK, "search", strFilePath + ", 标题：" + title);
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
        } finally {
            try {
                if (fwriter != null) {
                    fwriter.flush();
                    fwriter.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean saveAsFileWriter(String content, String savefile) {
        FileWriter fwriter = null;
        boolean isSuccess = false;
        try {
            fwriter = new FileWriter(savefile);
            fwriter.write(content);
            isSuccess = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return isSuccess;
    }

    public static String readFile(String filePath) {
        BufferedReader br = null;
        StringBuffer buffer = new StringBuffer();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                //System.out.println(line);
                buffer.append(line);
                buffer.append("\n");
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                br = null;
            }
        }
        return buffer.toString();
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
