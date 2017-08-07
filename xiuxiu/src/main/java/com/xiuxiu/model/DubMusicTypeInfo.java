package com.xiuxiu.model;

import java.util.List;

/**
 * Created by hzdykj on 2017/6/14.
 */

public class DubMusicTypeInfo {

    /**
     * result : [{"parid":"MQ==","subid":"MQ==","subtit":"5o6o6I2Q"},{"parid":"MQ==","subid":"Mg==","subtit":"5Zeo6LW35p2l"},{"parid":"MQ==","subid":"Mw==","subtit":"5b+r5LmQ"},{"parid":"MQ==","subid":"NA==","subtit":"5Lyk5oSf"},{"parid":"MQ==","subid":"NQ==","subtit":"5a6J6Z2Z"}]
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
         * parid : MQ==
         * subid : MQ==
         * subtit : 5o6o6I2Q
         */

        private String parid;
        private String subid;
        private String subtit;

        public String getParid() {
            return parid;
        }

        public void setParid(String parid) {
            this.parid = parid;
        }

        public String getSubid() {
            return subid;
        }

        public void setSubid(String subid) {
            this.subid = subid;
        }

        public String getSubtit() {
            return subtit;
        }

        public void setSubtit(String subtit) {
            this.subtit = subtit;
        }
    }
}
