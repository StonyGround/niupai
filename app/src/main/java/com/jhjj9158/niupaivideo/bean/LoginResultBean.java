package com.jhjj9158.niupaivideo.bean;

import java.util.List;

/**
 * Created by pc on 17-4-5.
 */

public class LoginResultBean {
    /**
     * code : 100
     * msg : 成功！
     * data : [{Contact.USERIDX:1628330494,"oldidx":1628330494,"password":"624d6ea7",
     * "oldid":"1628330494","platforid":0}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * useridx : 1628330494
         * oldidx : 1628330494
         * password : 624d6ea7
         * oldid : 1628330494
         * platforid : 0
         */

        private int useridx;
        private int oldidx;
        private String password;
        private String oldid;
        private int platforid;

        public int getUseridx() {
            return useridx;
        }

        public void setUseridx(int useridx) {
            this.useridx = useridx;
        }

        public int getOldidx() {
            return oldidx;
        }

        public void setOldidx(int oldidx) {
            this.oldidx = oldidx;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getOldid() {
            return oldid;
        }

        public void setOldid(String oldid) {
            this.oldid = oldid;
        }

        public int getPlatforid() {
            return platforid;
        }

        public void setPlatforid(int platforid) {
            this.platforid = platforid;
        }
    }
}
