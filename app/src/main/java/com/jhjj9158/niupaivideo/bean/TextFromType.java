package com.jhjj9158.niupaivideo.bean;

import java.util.List;

/**
 * Created by oneki on 2017/6/7.
 */

public class TextFromType {
    /**
     * result : [{"fromtype":3,"tcontent":"欢乐直播"},{"fromtype":11,"tcontent":"水晶直播"}]
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
         * fromtype : 3
         * tcontent : 欢乐直播
         */

        private int fromtype;
        private String tcontent;

        public int getFromtype() {
            return fromtype;
        }

        public void setFromtype(int fromtype) {
            this.fromtype = fromtype;
        }

        public String getTcontent() {
            return tcontent;
        }

        public void setTcontent(String tcontent) {
            this.tcontent = tcontent;
        }
    }
}
