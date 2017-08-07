package com.jhjj9158.niupaivideo.bean;

import java.util.List;

/**
 * Created by oneki on 2017/4/28.
 */

public class LikeBean {

    /**
     * result : [{"vid":519,"uidx":501697933,"nickName":"5qyi5LmQ5ZCn55u05pKtLeWwj+e+jg==","tContent":"6LWe5LqG6L+Z5Liq6KeG6aKR","videoPicUrl":"aHR0cDovL2ltYWdlLnF1bGlhby5jb20vMjAxNzA0MTMvQjFBNkM3RDg4MDZDODc4NzBGMjYwRTA2NDQ3MUQ4QjYxLmpwZw==","cDate":"MDQtMTc=","isread":1},{"vid":611,"uidx":501697933,"nickName":"5qyi5LmQ5ZCn55u05pKtLeWwj+e+jg==","tContent":"6LWe5LqG6L+Z5Liq6KeG6aKR","videoPicUrl":"aHR0cDovL2ltYWdlLnF1bGlhby5jb20vMjAxNzA0MTUvQzk4RjAyMjI0MUM4MUVBQjUwQTIwREI2QkRBMTUwOTkxLmpwZw==","cDate":"MDQtMTc=","isread":1},{"vid":185,"uidx":1627968076,"nickName":"Q29saW4=","tContent":"6LWe5LqG6L+Z5Liq6KeG6aKR","videoPicUrl":"aHR0cDovL2ltYWdlLnF1bGlhby5jb20vMjAxNzA0MTAvMUQ3NjM4MDY1OUI4QkM2QzNGOEY0MkM5QUVCRDFGMkUxLmpwZw==","cDate":"MDQtMTE=","isread":1}]
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

    public static class ResultBean {
        /**
         * vid : 519
         * uidx : 501697933
         * nickName : 5qyi5LmQ5ZCn55u05pKtLeWwj+e+jg==
         * tContent : 6LWe5LqG6L+Z5Liq6KeG6aKR
         * videoPicUrl : aHR0cDovL2ltYWdlLnF1bGlhby5jb20vMjAxNzA0MTMvQjFBNkM3RDg4MDZDODc4NzBGMjYwRTA2NDQ3MUQ4QjYxLmpwZw==
         * cDate : MDQtMTc=
         * isread : 1
         */

        private int vid;
        private int uidx;
        private String nickName;
        private String tContent;
        private String videoPicUrl;
        private String cDate;
        private int isread;
        private String comment;

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

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getTContent() {
            return tContent;
        }

        public void setTContent(String tContent) {
            this.tContent = tContent;
        }

        public String getVideoPicUrl() {
            return videoPicUrl;
        }

        public void setVideoPicUrl(String videoPicUrl) {
            this.videoPicUrl = videoPicUrl;
        }

        public String getCDate() {
            return cDate;
        }

        public void setCDate(String cDate) {
            this.cDate = cDate;
        }

        public int getIsread() {
            return isread;
        }

        public void setIsread(int isread) {
            this.isread = isread;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
