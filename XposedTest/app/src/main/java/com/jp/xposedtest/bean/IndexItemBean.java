package com.jp.xposedtest.bean;

public class IndexItemBean {
    public String title;
    public String url;
    public int mPosition;
    public int mIndex;
    public int style_type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public int getmIndex() {
        return mIndex;
    }

    public void setmIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public int getStyle_type() {
        return style_type;
    }

    public void setStyle_type(int style_type) {
        this.style_type = style_type;
    }

    @Override
    public String toString() {
        return "IndexItemBean{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", mPosition=" + mPosition +
                ", mIndex=" + mIndex +
                ", style_type=" + style_type +
                '}';
    }
}
