package com.jhjj9158.niupaivideo.bean;

import java.util.List;

/**
 * Created by oneki on 2017/5/20.
 */

public class WithdrawHistoryBean {
    /**
     * result : [{"id":14,"uidx":1628330494,"nickname":"MTIzMzQ=","alipay":"MTc3MTgzMDg0Njk=","alipayname":"","wallet":"MC4wMA==",
     * "applytime":"5pio5aSpMjE6NDA=","flg":0,"isread":0}]
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
         * id : 14
         * uidx : 1628330494
         * nickname : MTIzMzQ=
         * alipay : MTc3MTgzMDg0Njk=
         * alipayname :
         * wallet : MC4wMA==
         * applytime : 5pio5aSpMjE6NDA=
         * flg : 0
         * isread : 0
         */

        private int id;
        private int uidx;
        private String nickname;
        private String alipay;
        private String alipayname;
        private String wallet;
        private String applytime;
        private int flg;
        private int isread;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUidx() {
            return uidx;
        }

        public void setUidx(int uidx) {
            this.uidx = uidx;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAlipay() {
            return alipay;
        }

        public void setAlipay(String alipay) {
            this.alipay = alipay;
        }

        public String getAlipayname() {
            return alipayname;
        }

        public void setAlipayname(String alipayname) {
            this.alipayname = alipayname;
        }

        public String getWallet() {
            return wallet;
        }

        public void setWallet(String wallet) {
            this.wallet = wallet;
        }

        public String getApplytime() {
            return applytime;
        }

        public void setApplytime(String applytime) {
            this.applytime = applytime;
        }

        public int getFlg() {
            return flg;
        }

        public void setFlg(int flg) {
            this.flg = flg;
        }

        public int getIsread() {
            return isread;
        }

        public void setIsread(int isread) {
            this.isread = isread;
        }
    }
}
