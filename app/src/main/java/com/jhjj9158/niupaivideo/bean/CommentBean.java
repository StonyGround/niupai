package com.jhjj9158.niupaivideo.bean;

import java.util.List;

/**
 * Created by pc on 17-4-22.
 */

public class CommentBean {
    /**
     * total : 1
     * result : [{"cid":135,"uidx":1628265072,"nickName":"6JKL5bu66I2j","buidx":1628265072,
     * "bnickName":"6JKL5bu66I2j","headphoto":"aHR0c","comment":"5bGF54S26L",
     * "longitude":120.103096008301,"latitude":30.3023509979248,"cDate":"MDQtMTA=","identify":0}]
     * errorcode : 00000:ok
     */

    private int total;
    private String errorcode;
    private List<ResultBean> result;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

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

    public static class ResultBean {
        /**
         * cid : 135
         * uidx : 1628265072
         * nickName : 6JKL5bu66I2j
         * buidx : 1628265072
         * bnickName : 6JKL5bu66I2j
         * headphoto : aHR0c
         * comment : 5bGF54S26L
         * longitude : 120.103096008301
         * latitude : 30.3023509979248
         * cDate : MDQtMTA=
         * identify : 0
         */

        private int cid;
        private int uidx;
        private String nickName;
        private int buidx;
        private String bnickName;
        private String headphoto;
        private String comment;
        private double longitude;
        private double latitude;
        private String cDate;
        private int identify;

        public int getCid() {
            return cid;
        }

        public void setCid(int cid) {
            this.cid = cid;
        }

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

        public int getBuidx() {
            return buidx;
        }

        public void setBuidx(int buidx) {
            this.buidx = buidx;
        }

        public String getBnickName() {
            return bnickName;
        }

        public void setBnickName(String bnickName) {
            this.bnickName = bnickName;
        }

        public String getHeadphoto() {
            return headphoto;
        }

        public void setHeadphoto(String headphoto) {
            this.headphoto = headphoto;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
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

        public String getCDate() {
            return cDate;
        }

        public void setCDate(String cDate) {
            this.cDate = cDate;
        }

        public int getIdentify() {
            return identify;
        }

        public void setIdentify(int identify) {
            this.identify = identify;
        }
    }
}
