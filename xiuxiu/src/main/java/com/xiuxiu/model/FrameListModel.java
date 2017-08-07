package com.xiuxiu.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oneki on 2017/7/3.
 */

public class FrameListModel implements Parcelable {
    private List<Long> frameList;

    public List<Long> getFrameList() {
        return frameList;
    }

    public void setFrameList(List<Long> frameList) {
        this.frameList = frameList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.frameList);
    }

    public FrameListModel() {
    }

    protected FrameListModel(Parcel in) {
        this.frameList = new ArrayList<Long>();
        in.readList(this.frameList, Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<FrameListModel> CREATOR = new Parcelable.Creator<FrameListModel>() {
        @Override
        public FrameListModel createFromParcel(Parcel source) {
            return new FrameListModel(source);
        }

        @Override
        public FrameListModel[] newArray(int size) {
            return new FrameListModel[size];
        }
    };
}
