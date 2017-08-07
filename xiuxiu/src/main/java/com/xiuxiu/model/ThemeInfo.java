package com.xiuxiu.model;

import org.simpleframework.xml.Root;


/**
 * Created by hzdykj on 2017/6/21.
 */
@Root
public class ThemeInfo {
//    private ThemeBean theme;
//
//    public ThemeBean getTheme() {
//        return theme;
//    }
//
//    public void setTheme(ThemeBean theme) {
//        this.theme = theme;
//    }
//
//    @Root(name = "theme")
//    public static class ThemeBean {


    private String themeid;
    private String themeName;
    private String themeUrl;
    private String themeZip;

//    public boolean isDownload() {
//        return isDownload;
//    }
//
//    public void setDownload(boolean download) {
//        isDownload = download;
//    }
//
//    private boolean isDownload;

    public ThemeInfo() {
    }
    public ThemeInfo(String themeid, String themeName, String themeUrl, String themeZip){
        super();
        this.themeid = themeid;
        this.themeName = themeName;
        this.themeUrl = themeUrl;
        this.themeZip = themeZip;
    }

    public String getThemeid() {
        return themeid;
    }

    public void setThemeid(String themeid) {
        this.themeid = themeid;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getThemeUrl() {
        return themeUrl;
    }

    public void setThemeUrl(String themeUrl) {
        this.themeUrl = themeUrl;
    }

    public String getThemeZip() {
        return themeZip;
    }

    public void setThemeZip(String themeZip) {
        this.themeZip = themeZip;
    }
//    }

}
