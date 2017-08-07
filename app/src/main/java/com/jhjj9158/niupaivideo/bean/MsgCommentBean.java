package com.jhjj9158.niupaivideo.bean;

import java.util.List;

/**
 * Created by oneki on 2017/4/27.
 */

public class MsgCommentBean {
    /**
     * result : [{"cid":147,"uidx":1627972055,"vid":184,"nickName":"ZGVzcGVyYWRv","comment":"5oiRYmhzaGRoc2g=","videoPicUrl":"aHR0cDovL","cDate":"MDQtMTE=","identify":0,"isread":0,"replycomment":"","headphoto":""}]
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
         * cid : 147
         * uidx : 1627972055
         * vid : 184
         * nickName : ZGVzcGVyYWRv
         * comment : 5oiRYmhzaGRoc2g=
         * videoPicUrl : aHR0cDovL
         * cDate : MDQtMTE=
         * identify : 0
         * isread : 0
         * replycomment :
         * headphoto :
         */

        private int cid;
        private int uidx;
        private int vid;
        private String nickName;
        private String comment;
        private String videoPicUrl;
        private String cDate;
        private int identify;
        private int isread;
        private String replycomment;
        private String headphoto;

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

        public int getVid() {
            return vid;
        }

        public void setVid(int vid) {
            this.vid = vid;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
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

        public int getIdentify() {
            return identify;
        }

        public void setIdentify(int identify) {
            this.identify = identify;
        }

        public int getIsread() {
            return isread;
        }

        public void setIsread(int isread) {
            this.isread = isread;
        }

        public String getReplycomment() {
            return replycomment;
        }

        public void setReplycomment(String replycomment) {
            this.replycomment = replycomment;
        }

        public String getHeadphoto() {
            return headphoto;
        }

        public void setHeadphoto(String headphoto) {
            this.headphoto = headphoto;
        }
    }
}
