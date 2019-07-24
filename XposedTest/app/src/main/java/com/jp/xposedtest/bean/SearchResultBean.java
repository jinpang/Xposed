package com.jp.xposedtest.bean;

/**
 * @projectName: XposedTest
 * @package: com.jp.xposedtest.bean
 * @className: SearchResultBean
 * @description: java类作用描述
 * @author: pangjingzhong
 * @email: jingzhongp@gmail.com
 * @createDate: 2019/7/24 16:15
 * @updateUser: 更新者
 * @updateDate: 2019/7/24 16:15
 * @updateRemark: 更新说明
 * @version: 1.0
 * @copyright: 2018-2019 (C)深圳市冰禾网络科技有限公司 Inc. All rights reserved.
 */
public class SearchResultBean {
    String key;
    String title;
    String content;
    String url;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
