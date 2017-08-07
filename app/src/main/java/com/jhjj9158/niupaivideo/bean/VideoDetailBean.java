package com.jhjj9158.niupaivideo.bean;

/**
 * Created by pc on 17-4-20.
 */

public class VideoDetailBean {
    /**
     * result : {"vid":184,"uidx":1628007796,"nickname":"eHdsMTIz",
     * "headphoto":"ZG93bi50aWFuZ2UuY29tL0NvbnRlbnRVc2VyLzIwMTcvMDQvMTMvMTgvMjIvMTYvMG5pdXBhaTAvMTYyODAwNzc5Nm5pdXBhaTAuanBn","phonebrand":"aXBob25l","videoUrl":"aHR0cDovL3ZpZGVvLnF1bGlhby5jb20vMjAxNzA0MTAvRkFENkZGNDYyOTYzRTQyNUM4OTk5Q0MyNENERTc5OUUubXA0","videoPicUrl":"aHR0cDovL2ltYWdlLnF1bGlhby5jb20vMjAxNzA0MTAvMjkzQzNCMzRGMjhDNUNCRjY5OTM4NTY1NTRFQUM4NEQuanBn","descriptions":"5YiG5Lqr","playNum":49,"goodNum":2,"cNum":2,"sNum":0,"tags":"5re75Yqg5qCH562+","createTime":"MDTmnIgxMOaXpTEwOjIw","zbtime":"VGEgMDTmnIgxMOaXpTEwOjIw5Zyo44CQ5rC05pm255u05pKt44CR5byA5pKt","longitude":120.366561889648,"latitude":30.3127670288086,"isFollow":0,"states":"5Yia55m75b2V6L+H","praiseCount":0,"area":"(null)"}
     * errorcode : 00000:ok
     */

    private ResultBean result;
    private String errorcode;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public static class ResultBean {
        /**
         * vid : 184
         * uidx : 1628007796
         * nickname : eHdsMTIz
         * headphoto :
         * ZG93bi50aWFuZ2UuY29tL0NvbnRlbnRVc2VyLzIwMTcvMDQvMTMvMTgvMjIvMTYvMG5pdXBhaTAvMTYyODAwNzc5Nm5pdXBhaTAuanBn
         * phonebrand : aXBob25l
         * videoUrl :
         * aHR0cDovL3ZpZGVvLnF1bGlhby5jb20vMjAxNzA0MTAvRkFENkZGNDYyOTYzRTQyNUM4OTk5Q0MyNENERTc5OUUubXA0
         * videoPicUrl :
         * aHR0cDovL2ltYWdlLnF1bGlhby5jb20vMjAxNzA0MTAvMjkzQzNCMzRGMjhDNUNCRjY5OTM4NTY1NTRFQUM4NEQuanBn
         * descriptions : 5YiG5Lqr
         * playNum : 49
         * goodNum : 2
         * cNum : 2
         * sNum : 0
         * tags : 5re75Yqg5qCH562+
         * createTime : MDTmnIgxMOaXpTEwOjIw
         * zbtime : VGEgMDTmnIgxMOaXpTEwOjIw5Zyo44CQ5rC05pm255u05pKt44CR5byA5pKt
         * longitude : 120.366561889648
         * latitude : 30.3127670288086
         * isFollow : 0
         * states : 5Yia55m75b2V6L+H
         * praiseCount : 0
         * area : (null)
         */

        private int vid;
        private int uidx;
        private String nickname;
        private String headphoto;
        private String phonebrand;
        private String videoUrl;
        private String videoPicUrl;
        private String descriptions;
        private int playNum;
        private int goodNum;
        private int cNum;
        private int sNum;
        private String tags;
        private String createTime;
        private String zbtime;
        private double longitude;
        private double latitude;
        private int isFollow;
        private String states;
        private int praiseCount;
        private String area;

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

        public String getDescriptions() {
            return descriptions;
        }

        public void setDescriptions(String descriptions) {
            this.descriptions = descriptions;
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

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getZbtime() {
            return zbtime;
        }

        public void setZbtime(String zbtime) {
            this.zbtime = zbtime;
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

        public int getIsFollow() {
            return isFollow;
        }

        public void setIsFollow(int isFollow) {
            this.isFollow = isFollow;
        }

        public String getStates() {
            return states;
        }

        public void setStates(String states) {
            this.states = states;
        }

        public int getPraiseCount() {
            return praiseCount;
        }

        public void setPraiseCount(int praiseCount) {
            this.praiseCount = praiseCount;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }
    }
}
