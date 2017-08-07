package com.xiuxiu.model;

/**
 * Created by hzdykj on 2017/6/23.
 */

public class ThemePhotoItemInfo {
    private int  id;
    private boolean isSelected;//是否选中

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ThemePhotoItemInfo(){

    }

    public ThemePhotoItemInfo(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
