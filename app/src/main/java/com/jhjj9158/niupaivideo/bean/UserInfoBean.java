package com.jhjj9158.niupaivideo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by pc on 17-4-12.
 */

public class UserInfoBean {

    /**
     * code : 100
     * msg : 成功
     * data : [{Contact.USERIDX:1628330494,"follownum":"0","headimg":"http://q.qlogo
     * .cn/qqapp/1105995205/B005D5883DFA0ADD8CC1B3D9D2EF48CB/100","nickName":"过把瘾就死",
     * "userSex":"1","fans":"0","userTrueName":""}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Parcelable {
        /**
         * useridx : 1628330494
         * follownum : 0
         * headimg : http://q.qlogo.cn/qqapp/1105995205/B005D5883DFA0ADD8CC1B3D9D2EF48CB/100
         * nickName : 过把瘾就死
         * userSex : 1
         * fans : 0
         * userTrueName :
         */

        private int useridx;
        private String follownum;
        private String headimg;
        private String nickName;
        private String userSex;
        private String fans;
        private String userTrueName;

        public int getUseridx() {
            return useridx;
        }

        public void setUseridx(int useridx) {
            this.useridx = useridx;
        }

        public String getFollownum() {
            return follownum;
        }

        public void setFollownum(String follownum) {
            this.follownum = follownum;
        }

        public String getHeadimg() {
            return headimg;
        }

        public void setHeadimg(String headimg) {
            this.headimg = headimg;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getUserSex() {
            return userSex;
        }

        public void setUserSex(String userSex) {
            this.userSex = userSex;
        }

        public String getFans() {
            return fans;
        }

        public void setFans(String fans) {
            this.fans = fans;
        }

        public String getUserTrueName() {
            return userTrueName;
        }

        public void setUserTrueName(String userTrueName) {
            this.userTrueName = userTrueName;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.useridx);
            dest.writeString(this.follownum);
            dest.writeString(this.headimg);
            dest.writeString(this.nickName);
            dest.writeString(this.userSex);
            dest.writeString(this.fans);
            dest.writeString(this.userTrueName);
        }

        public DataBean() {
        }

        protected DataBean(Parcel in) {
            this.useridx = in.readInt();
            this.follownum = in.readString();
            this.headimg = in.readString();
            this.nickName = in.readString();
            this.userSex = in.readString();
            this.fans = in.readString();
            this.userTrueName = in.readString();
        }

        public static final Parcelable.Creator<DataBean> CREATOR = new Parcelable.Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel source) {
                return new DataBean(source);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };
    }
}
