package com.jhjj9158.niupaivideo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by pc on 17-4-25.
 */

public class FollowBean {
    /**
     * result : [{"uidx":1628007796,"nickName":"eHdsMTIz","signature":"","headphoto":"ZG93bi50",
     * "isFollow":0},{"uidx":1627968076,"nickName":"Q29saW4=","signature":"",
     * "headphoto":"aHR0cDovL3","isFollow":0}]
     * errorcode : 00000:ok
     */

    private String errorcode;
    private List<ResultBean> result;

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean implements Parcelable {
        /**
         * uidx : 1628007796
         * nickName : eHdsMTIz
         * signature :
         * headphoto : ZG93bi50
         * isFollow : 0
         */

        private int uidx;
        private String nickName;
        private String signature;
        private String headphoto;
        private int isFollow;

        public int getUidx() {
            return uidx;
        }

        public void setUidx(int uidx) {
            this.uidx = uidx;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getHeadphoto() {
            return headphoto;
        }

        public void setHeadphoto(String headphoto) {
            this.headphoto = headphoto;
        }

        public int getIsFollow() {
            return isFollow;
        }

        public void setIsFollow(int isFollow) {
            this.isFollow = isFollow;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.uidx);
            dest.writeString(this.nickName);
            dest.writeString(this.signature);
            dest.writeString(this.headphoto);
            dest.writeInt(this.isFollow);
        }

        public ResultBean() {
        }

        protected ResultBean(Parcel in) {
            this.uidx = in.readInt();
            this.nickName = in.readString();
            this.signature = in.readString();
            this.headphoto = in.readString();
            this.isFollow = in.readInt();
        }

        public static final Parcelable.Creator<ResultBean> CREATOR = new Parcelable.Creator<ResultBean>() {
            @Override
            public ResultBean createFromParcel(Parcel source) {
                return new ResultBean(source);
            }

            @Override
            public ResultBean[] newArray(int size) {
                return new ResultBean[size];
            }
        };
    }
}
