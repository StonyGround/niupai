package com.xiuxiu.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by hzdykj on 2017/7/3.
 */
@Root(name = "theme")
public class ThemeResult {
    @Element(name = "themeid")
    private int themeid;
    @Element(name = "themeName")
    private String themeName;
    @Element(name = "themeUrl")
    private String themeUrl;
    @Element(name = "themeZip")
    private String themeZip;

    public int getThemeid() {
        return themeid;
    }

    public void setThemeid(int themeid) {
        this.themeid = themeid;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeNamw) {
        this.themeName = themeNamw;
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
}
