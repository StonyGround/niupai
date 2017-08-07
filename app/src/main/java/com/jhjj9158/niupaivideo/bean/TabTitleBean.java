package com.jhjj9158.niupaivideo.bean;

import java.util.List;

/**动态tab
 * Created by pc on 17-4-17.
 */

public class TabTitleBean {
    /**
     * result : [{"vrid":"MQ==","vrshow":"MA==","vrname":"5pCe56yR","vrsort":"MA=="}]
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
         * vrid : MQ==
         * vrshow : MA==
         * vrname : 5pCe56yR
         * vrsort : MA==
         */

        private String vrid;
        private String vrshow;
        private String vrname;
        private String vrsort;

        public String getVrid() {
            return vrid;
        }

        public void setVrid(String vrid) {
            this.vrid = vrid;
        }

        public String getVrshow() {
            return vrshow;
        }

        public void setVrshow(String vrshow) {
            this.vrshow = vrshow;
        }

        public String getVrname() {
            return vrname;
        }

        public void setVrname(String vrname) {
            this.vrname = vrname;
        }

        public String getVrsort() {
            return vrsort;
        }

        public void setVrsort(String vrsort) {
            this.vrsort = vrsort;
        }
    }
}
