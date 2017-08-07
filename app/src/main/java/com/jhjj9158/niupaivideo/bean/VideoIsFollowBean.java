package com.jhjj9158.niupaivideo.bean;

import java.util.List;

/**
 * Created by pc on 17-4-21.
 */

public class VideoIsFollowBean {

    /**
     * result : [{"goodnum":6,"cnum":1,"snum":0,"isfollow":0}]
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
         * goodnum : 6
         * cnum : 1
         * snum : 0
         * isfollow : 0
         */

        private int goodnum;
        private int cnum;
        private int snum;
        private int isfollow;

        public int getGoodnum() {
            return goodnum;
        }

        public void setGoodnum(int goodnum) {
            this.goodnum = goodnum;
        }

        public int getCnum() {
            return cnum;
        }

        public void setCnum(int cnum) {
            this.cnum = cnum;
        }

        public int getSnum() {
            return snum;
        }

        public void setSnum(int snum) {
            this.snum = snum;
        }

        public int getIsfollow() {
            return isfollow;
        }

        public void setIsfollow(int isfollow) {
            this.isfollow = isfollow;
        }
    }
}
