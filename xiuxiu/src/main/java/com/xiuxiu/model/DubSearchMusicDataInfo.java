package com.xiuxiu.model;

import java.util.List;

/**
 * Created by hzdykj on 2017/6/14.
 */

public class DubSearchMusicDataInfo {


    /**
     * result : [{"title":"5piv5pe25YCZ6KGo5ryU55yf5q2j55qE5oqA5pyv5LqG","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI2L0I4QzFCQUVFOEZCMUU0NzdBQzY2OURGMzJDOEQwOTkxLm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"5LiN6KaB5b+Y","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1LzU1QTA0MDBDOTlGRTYyNTA0ODM2MTQzNDY4Q0Q3QTg2Lm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"QmFieQ==","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1LzNBRjA0NzhDQzNEMzBGQjJDNTkxNzYwMDE0NTA5MDUyLm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"5ZiJ56a+6Iie5puy","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1LzA4QTFEQUQ5QTY3N0JDREJGOEQ1MDM5MTk3NzNGQUYwLm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"5aW95rGJ5q2M","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1LzlCRkUxQ0U0MEQ4MDM5QjAwNDQ2NDg3OTA2NTNFREU3Lm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"5ZKW5Zax5ZKW5Zax","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1L0QwMkMxOTQ3ODhEOTAzRjBGQUU3M0IzMjkzNEVERTBDLm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"5b+Q5b+R","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1LzMyRkRGRTkyNzJFNDdERDBFRkQ1MjA1NTBFQTFFQjQwLm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"SGV5IFlvdSBUaGVyZQ==","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1LzhDRjc2MzY3QjZFRTY1NUJEMDFBNDRGMEQzMTkyM0Q5Lm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"5ZKa5ZKa5ZKa","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1LzYzRTFCRDYxNjEyN0UzQTc4MTEwMDIwMjZBNDEyNEM0Lm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"TG9vayBhdCBNZSBOb3c=","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1LzhCOTUyMkRDMDE0RDBBRTRBNjUzRDlBOTlDQTc1NEU5Lm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"RE1YIC0gV2hlcmUgdGhlIEhvb2QgQXQ=","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1LzY0Q0I3NEUzRDA0OTA4RTg5NjQ0MUY1REQ4RkQ2Q0I1Lm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"QXZyaWwgTGF2aWduZSAtIEhlbGxvIEtpdHR5","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1Lzg2MzdCMkM1N0JDQjlFMENGRUE2QjBDMDI1NDVDNEU0Lm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"QS1UZWVucyAtIEZsb29yZmlsbGVy","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1LzcwQzc5MzJFRjNBMTNDNURDQjg5Mzg5RjMwODdBOUYwLm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"VFdJQ0UgLSBUVA==","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1L0REODA5OEFDOUMzQzg2NTM5MkI0RUU5NDk3QTQ2QTk2Lm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"Qk9PTQ==","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1L0Y5MzUyRTRDRjA5MDlFRTEwMDgyNEQ2QzczNDRGQTFCLm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"TWUgdG9v","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1LzkwOTVEM0RDMzBCMUNERjVFMDUyNjNCNEVFNDkyQzk3Lm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"5YWo6YOo6YO95piv5L2g","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1LzE4Q0YxODBCQjhDQkE1MjIyNkRCMDI3RThCMTdFNTEyLm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"VGV6IENhZGV5IC0gU2V2ZQ==","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1L0QyRDcwOTQzMTZGNTY5NUFCQjM4OTZDNUI2NEJCNzA0Lm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"V2FubmFiZQ==","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1LzYzNDc2MUIzQjY2ODhDQUYwMTY0NDUzNEJCOUExOUI2Lm1wMw==","bzaudiourl":"","lraudio":"","mtype":0},{"title":"Q2FsbCBvZiB0aGUgYW1idWxhbmNl","imgUrl":"","audioUrl":"aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI1L0FBRTU0MUQ0NEE5ODUyOTZEQkEzRTFBNEM2NkY0ODcxLm1wMw==","bzaudiourl":"","lraudio":"","mtype":0}]
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
         * title : 5piv5pe25YCZ6KGo5ryU55yf5q2j55qE5oqA5pyv5LqG
         * imgUrl :
         * audioUrl : aHR0cDovL3NyLnYuc2psaXZlLmNuL2F1ZGlvLzIwMTcwNTI2L0I4QzFCQUVFOEZCMUU0NzdBQzY2OURGMzJDOEQwOTkxLm1wMw==
         * bzaudiourl :
         * lraudio :
         * mtype : 0
         */

        private String title;
        private String imgUrl;
        private String audioUrl;
        private String bzaudiourl;
        private String lraudio;
        private int mtype;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        private boolean isSelected;//是否选中

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getAudioUrl() {
            return audioUrl;
        }

        public void setAudioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
        }

        public String getBzaudiourl() {
            return bzaudiourl;
        }

        public void setBzaudiourl(String bzaudiourl) {
            this.bzaudiourl = bzaudiourl;
        }

        public String getLraudio() {
            return lraudio;
        }

        public void setLraudio(String lraudio) {
            this.lraudio = lraudio;
        }

        public int getMtype() {
            return mtype;
        }

        public void setMtype(int mtype) {
            this.mtype = mtype;
        }
    }
}
