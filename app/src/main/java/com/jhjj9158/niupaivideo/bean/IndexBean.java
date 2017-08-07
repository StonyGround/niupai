package com.jhjj9158.niupaivideo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * 首页
 * Created by pc on 17-4-13.
 */

public class IndexBean {
    /**
     * result : [{"vid":531,"uidx":500553062,"showuidx":89836,
     * "videoUrl":"aHR0cDovL3ZpZGVvLnF1bGlhby5jb20vMjAxNzA0MTMvRTAxMzE1QTlGOEIyMTAwMjhCNUJEOTlCOUNFMzk3NDcubXA0","videoPicUrl":"aHR0cDovL2ltYWdlLnF1bGlhby5jb20vMjAxNzA0MTMvNkU0MTEwRjM5NTMxQjZCQkJGNTg3ODk0MTQwOEU4MUIuanBn","playNum":0,"goodNum":0,"cNum":0,"sNum":0,"nickname":"ODk4MzY=","headphoto":"ZG93bi50aWFuZ2UuY29tL0NvbnRlbnRVc2VyLzIwMTcvMDQvMTEvMTUvMTcvMjQvMG5pdXBhaTAvNTAwNTUzMDYybml1cGFpMC5qcGc=","phonebrand":"aXBob25l","descriptions":"JUU1JThGJThDJUU1JTg3JUJCJUU1JThGJThDJUU1JTg3JUJCJUU1JThGJThDJUU1JTg3JUJCJUVGJUJDJThDJUU5JTg3JThEJUU4JUE2JTgxJUU3JTlBJTg0JUU0JUJBJThCJUU2JTgzJTg1JUU4JUFGJUI0JUU0JUI4JTg5JUU5JTgxJThE","area":"KG51bGwp","isFollow":0,"longitude":119.637489318848,"latitude":29.0830745697021,"signatures":0,"states":"5Zyo57q/","createTime":"MeWIhumSn+WJjQ==","zbtime":"VGEg6L+Y5pyq5Zyo44CQ5rC05pm255u05pKt44CR5byA5pKt","praiseCount":0,"videoSize":"3.813904","imgScale":"0.750000","fromtype":11},{"vid":529,"uidx":500553062,"showuidx":89836,"videoUrl":"aHR0cDovL3ZpZGVvLnF1bGlhby5jb20vMjAxNzA0MTMvRkVGRkI1MkUzQTJBMURERjZDMzdCNjZFMjZFODEwNDIubXA0","videoPicUrl":"aHR0cDovL2ltYWdlLnF1bGlhby5jb20vMjAxNzA0MTMvN0ZENEExNTBBQUI0MTJGOEMwNzlBRkZDNjI5REY4RDMuanBn","playNum":0,"goodNum":0,"cNum":0,"sNum":0,"nickname":"ODk4MzY=","headphoto":"ZG93bi50aWFuZ2UuY29tL0NvbnRlbnRVc2VyLzIwMTcvMDQvMTEvMTUvMTcvMjQvMG5pdXBhaTAvNTAwNTUzMDYybml1cGFpMC5qcGc=","phonebrand":"aXBob25l","descriptions":"NjY2NiVFNSU4RiU4QyVFNSU4NyVCQg==","area":"KG51bGwp","isFollow":0,"longitude":119.637489318848,"latitude":29.0830745697021,"signatures":0,"states":"5Zyo57q/","createTime":"M+WIhumSn+WJjQ==","zbtime":"VGEg6L+Y5pyq5Zyo44CQ5rC05pm255u05pKt44CR5byA5pKt","praiseCount":0,"videoSize":"4.119979","imgScale":"0.750000","fromtype":11}]
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
         * vid : 531
         * uidx : 500553062
         * showuidx : 89836
         * videoUrl :
         * aHR0cDovL3ZpZGVvLnF1bGlhby5jb20vMjAxNzA0MTMvRTAxMzE1QTlGOEIyMTAwMjhCNUJEOTlCOUNFMzk3NDcubXA0
         * videoPicUrl :
         * aHR0cDovL2ltYWdlLnF1bGlhby5jb20vMjAxNzA0MTMvNkU0MTEwRjM5NTMxQjZCQkJGNTg3ODk0MTQwOEU4MUIuanBn
         * playNum : 0
         * goodNum : 0
         * cNum : 0
         * sNum : 0
         * nickname : ODk4MzY=
         * headphoto :
         * ZG93bi50aWFuZ2UuY29tL0NvbnRlbnRVc2VyLzIwMTcvMDQvMTEvMTUvMTcvMjQvMG5pdXBhaTAvNTAwNTUzMDYybml1cGFpMC5qcGc=
         * phonebrand : aXBob25l
         * descriptions :
         * JUU1JThGJThDJUU1JTg3JUJCJUU1JThGJThDJUU1JTg3JUJCJUU1JThGJThDJUU1JTg3JUJCJUVGJUJDJThDJUU5JTg3JThEJUU4JUE2JTgxJUU3JTlBJTg0JUU0JUJBJThCJUU2JTgzJTg1JUU4JUFGJUI0JUU0JUI4JTg5JUU5JTgxJThE
         * area : KG51bGwp
         * isFollow : 0
         * longitude : 119.637489318848
         * latitude : 29.0830745697021
         * signatures : 0
         * states : 5Zyo57q/
         * createTime : MeWIhumSn+WJjQ==
         * zbtime : VGEg6L+Y5pyq5Zyo44CQ5rC05pm255u05pKt44CR5byA5pKt
         * praiseCount : 0
         * videoSize : 3.813904
         * imgScale : 0.750000
         * fromtype : 11
         */

        private int vid;
        private int uidx;
        private int showuidx;
        private String videoUrl;
        private String videoPicUrl;
        private int playNum;
        private int goodNum;
        private int cNum;
        private int sNum;
        private String nickname;
        private String headphoto;
        private String phonebrand;
        private String descriptions;
        private String area;
        private int isFollow;
        private double longitude;
        private double latitude;
        private int signatures;
        private String states;
        private String createTime;
        private String zbtime;
        private int praiseCount;
        private String videoSize;
        private String imgScale;
        private int fromtype;
        private int loginplant;
        private String imgscale;

        public int getVid() {
            return vid;
        }

        public void setVid(int vid) {
            this.vid = vid;
        }

        public int getUidx() {
            return uidx;
        }

        public void setUidx(int uidx) {
            this.uidx = uidx;
        }

        public int getShowuidx() {
            return showuidx;
        }

        public void setShowuidx(int showuidx) {
            this.showuidx = showuidx;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getVideoPicUrl() {
            return videoPicUrl;
        }

        public void setVideoPicUrl(String videoPicUrl) {
            this.videoPicUrl = videoPicUrl;
        }

        public int getPlayNum() {
            return playNum;
        }

        public void setPlayNum(int playNum) {
            this.playNum = playNum;
        }

        public int getGoodNum() {
            return goodNum;
        }

        public void setGoodNum(int goodNum) {
            this.goodNum = goodNum;
        }

        public int getCNum() {
            return cNum;
        }

        public void setCNum(int cNum) {
            this.cNum = cNum;
        }

        public int getSNum() {
            return sNum;
        }

        public void setSNum(int sNum) {
            this.sNum = sNum;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getHeadphoto() {
            return headphoto;
        }

        public void setHeadphoto(String headphoto) {
            this.headphoto = headphoto;
        }

        public String getPhonebrand() {
            return phonebrand;
        }

        public void setPhonebrand(String phonebrand) {
            this.phonebrand = phonebrand;
        }

        public String getDescriptions() {
            return descriptions;
        }

        public void setDescriptions(String descriptions) {
            this.descriptions = descriptions;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public int getIsFollow() {
            return isFollow;
        }

        public void setIsFollow(int isFollow) {
            this.isFollow = isFollow;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public int getSignatures() {
            return signatures;
        }

        public void setSignatures(int signatures) {
            this.signatures = signatures;
        }

        public String getStates() {
            return states;
        }

        public void setStates(String states) {
            this.states = states;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getZbtime() {
            return zbtime;
        }

        public void setZbtime(String zbtime) {
            this.zbtime = zbtime;
        }

        public int getPraiseCount() {
            return praiseCount;
        }

        public void setPraiseCount(int praiseCount) {
            this.praiseCount = praiseCount;
        }

        public String getVideoSize() {
            return videoSize;
        }

        public void setVideoSize(String videoSize) {
            this.videoSize = videoSize;
        }

        public String getImgScale() {
            return imgScale;
        }

        public void setImgScale(String imgScale) {
            this.imgScale = imgScale;
        }

        public int getFromtype() {
            return fromtype;
        }

        public void setFromtype(int fromtype) {
            this.fromtype = fromtype;
        }

        public int getLoginplant() {
            return loginplant;
        }

        public void setLoginplant(int loginplant) {
            this.loginplant = loginplant;
        }

        public String getImgscale() {
            return imgscale;
        }

        public void setImgscale(String imgscale) {
            this.imgscale = imgscale;
        }

        public ResultBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.vid);
            dest.writeInt(this.uidx);
            dest.writeInt(this.showuidx);
            dest.writeString(this.videoUrl);
            dest.writeString(this.videoPicUrl);
            dest.writeInt(this.playNum);
            dest.writeInt(this.goodNum);
            dest.writeInt(this.cNum);
            dest.writeInt(this.sNum);
            dest.writeString(this.nickname);
            dest.writeString(this.headphoto);
            dest.writeString(this.phonebrand);
            dest.writeString(this.descriptions);
            dest.writeString(this.area);
            dest.writeInt(this.isFollow);
            dest.writeDouble(this.longitude);
            dest.writeDouble(this.latitude);
            dest.writeInt(this.signatures);
            dest.writeString(this.states);
            dest.writeString(this.createTime);
            dest.writeString(this.zbtime);
            dest.writeInt(this.praiseCount);
            dest.writeString(this.videoSize);
            dest.writeString(this.imgScale);
            dest.writeInt(this.fromtype);
            dest.writeInt(this.loginplant);
            dest.writeString(this.imgscale);
        }

        protected ResultBean(Parcel in) {
            this.vid = in.readInt();
            this.uidx = in.readInt();
            this.showuidx = in.readInt();
            this.videoUrl = in.readString();
            this.videoPicUrl = in.readString();
            this.playNum = in.readInt();
            this.goodNum = in.readInt();
            this.cNum = in.readInt();
            this.sNum = in.readInt();
            this.nickname = in.readString();
            this.headphoto = in.readString();
            this.phonebrand = in.readString();
            this.descriptions = in.readString();
            this.area = in.readString();
            this.isFollow = in.readInt();
            this.longitude = in.readDouble();
            this.latitude = in.readDouble();
            this.signatures = in.readInt();
            this.states = in.readString();
            this.createTime = in.readString();
            this.zbtime = in.readString();
            this.praiseCount = in.readInt();
            this.videoSize = in.readString();
            this.imgScale = in.readString();
            this.fromtype = in.readInt();
            this.loginplant = in.readInt();
            this.imgscale = in.readString();
        }

        public static final Creator<ResultBean> CREATOR = new Creator<ResultBean>() {
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
