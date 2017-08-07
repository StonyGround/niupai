package com.jhjj9158.niupaivideo.bean;

import java.util.List;

/**
 * Created by oneki on 2017/5/1.
 */

public class BannerBean {
    /**
     * result : [{"gid":11,"appid":"","linkUrl":"aHR0c","advImg":"aHR0cDov","adverttype":"Mg==","tags":"MQ=="}]
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
         * gid : 11
         * appid :
         * linkUrl : aHR0c
         * advImg : aHR0cDov
         * adverttype : Mg==
         * tags : MQ==
         */

        private int gid;
        private String appid;
        private String linkUrl;
        private String advImg;
        private String adverttype;
        private String tags;

        public int getGid() {
            return gid;
        }

        public void setGid(int gid) {
            this.gid = gid;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getLinkUrl() {
            return linkUrl;
        }

        public void setLinkUrl(String linkUrl) {
            this.linkUrl = linkUrl;
        }

        public String getAdvImg() {
            return advImg;
        }

        public void setAdvImg(String advImg) {
            this.advImg = advImg;
        }

        public String getAdverttype() {
            return adverttype;
        }

        public void setAdverttype(String adverttype) {
            this.adverttype = adverttype;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }
    }
}
