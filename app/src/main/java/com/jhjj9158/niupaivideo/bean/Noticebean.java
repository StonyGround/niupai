package com.jhjj9158.niupaivideo.bean;

import java.util.List;

/**
 * Created by oneki on 2017/4/27.
 */

public class Noticebean {
    /**
     * result : [{"vid":519,"informcontent":"aHR0cDovL","url":"aHR0cD","type":1,"time":"",
     * "isread":1}]
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
         * informcontent : aHR0cDovL
         * url : aHR0cD
         * type : 1
         * time :
         * isread : 1
         */

        private int vid;
        private String informcontent;
        private String url;
        private int type;
        private String time;
        private int isread;

        public int getVid() {
            return vid;
        }

        public void setVid(int vid) {
            this.vid = vid;
        }

        public String getInformcontent() {
            return informcontent;
        }

        public void setInformcontent(String informcontent) {
            this.informcontent = informcontent;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getIsread() {
            return isread;
        }

        public void setIsread(int isread) {
            this.isread = isread;
        }
    }
}
