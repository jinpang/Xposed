package com.jp.xposedtest.utils;

/**
 * @projectName: XposedTest
 * @package: com.jp.xposedtest.utils
 * @className: Common
 * @description: java类作用描述
 * @author: pangjingzhong
 * @email: jingzhongp@gmail.com
 * @createDate: 2019/7/24 20:03
 * @updateUser: 更新者
 * @updateDate: 2019/7/24 20:03
 * @updateRemark: 更新说明
 * @version: 1.0
 * @copyright: 2018-2019 (C)深圳市冰禾网络科技有限公司 Inc. All rights reserved.
 */

import java.io.File;

public class Common {
    public static final String CLASS_PACKAGE_PARSER_PACKAGE = "android.content.pm.PackageParser.Package";
    public static final String[] MTP_APPS = {"com.android.MtpApplication", "com.samsung.android.MtpApplication"};
    public static final String PERM_ACCESS_ALL_EXTERNAL_STORAGE = "android.permission.ACCESS_ALL_EXTERNAL_STORAGE";
    public static final String PERM_WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final String PERM_WRITE_MEDIA_STORAGE = "android.permission.WRITE_MEDIA_STORAGE";

    private Common() {
    }

    public static String appendFileSeparator(String path) {
        if (!path.endsWith(File.separator)) {
            return path + File.separator;
        }
        return path;
    }
}