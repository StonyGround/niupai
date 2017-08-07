package com.jhjj9158.niupaivideo.bean;

/**
 * Created by pc on 17-4-24.
 */

public class PersonalBean {
    /**
     * result : {"uidx":1621297229,"showuidx":62493574,"nickName":"5oOF5rex5Ly85rW3","gender":0,
     * "signature":"","headphoto":"","bgphoto":"","phoneBrand":"MA==","vNum":5,"fansNum":0,
     * "followNum":0,"zanNum":0,"longitude":0,"latitude":0,"isFollow":0,"states":"MeWRqOWJjQ=="}
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
         * uidx : 1621297229
         * showuidx : 62493574
         * nickName : 5oOF5rex5Ly85rW3
         * gender : 0
         * signature :
         * headphoto :
         * bgphoto :
         * phoneBrand : MA==
         * vNum : 5
         * fansNum : 0
         * followNum : 0
         * zanNum : 0
         * longitude : 0
         * latitude : 0
         * isFollow : 0
         * states : MeWRqOWJjQ==
         */

        private int uidx;
        private int showuidx;
        private String nickName;
        private int gender;
        private String signature;
        private String headphoto;
        private String bgphoto;
        private String phoneBrand;
        private int vNum;
        private int fansNum;
        private int followNum;
        private int zanNum;
        private double longitude;
        private double latitude;
        private int isFollow;
        private String states;

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

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
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

        public String getBgphoto() {
            return bgphoto;
        }

        public void setBgphoto(String bgphoto) {
            this.bgphoto = bgphoto;
        }

        public String getPhoneBrand() {
            return phoneBrand;
        }

        public void setPhoneBrand(String phoneBrand) {
            this.phoneBrand = phoneBrand;
        }

        public int getVNum() {
            return vNum;
        }

        public void setVNum(int vNum) {
            this.vNum = vNum;
        }

        public int getFansNum() {
            return fansNum;
        }

        public void setFansNum(int fansNum) {
            this.fansNum = fansNum;
        }

        public int getFollowNum() {
            return followNum;
        }

        public void setFollowNum(int followNum) {
            this.followNum = followNum;
        }

        public int getZanNum() {
            return zanNum;
        }

        public void setZanNum(int zanNum) {
            this.zanNum = zanNum;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(int longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(int latitude) {
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
    }
}
