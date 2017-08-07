package com.jhjj9158.niupaivideo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pc on 17-4-13.
 */

public class UserDetailBean {

    /**
     * result : {"uidx":1628330494,"showuidx":1628330494,"fromtype":0,"password":"",
     * "nickName":"6L+H5oqK55i+5bCx5q27","gender":1,"signature":"",
     * "headphoto":"aHR0cDovL3EucWxvZ28uY24vcXFhcHAvMTEwNTk5NTIwNS9CMDA1RDU4ODNERkEwQUREOENDMUIzRDlEMkVGNDhDQi8xMDA=","bgphoto":"aHR0cDovL3EucWxvZ28uY24vcXFhcHAvMTEwNTk5NTIwNS9CMDA1RDU4ODNERkEwQUREOENDMUIzRDlEMkVGNDhDQi8xMDA=","wallet":0,"province":"5rW35aSW","city":"576O5Zu9","phoneBrand":"MA==","Binding":0,"source":0,"mobilePhone":"MA==","states":"MA==","lastLoginTime":"","vNum":0,"newComment":0,"comment":0,"fansNum":0,"followNum":0,"newZanNum":0,"zanNum":0,"newtNum":0,"tNum":0,"longitude":0,"latitude":0,"userborn":"19850101","alipay":"","alipayName":"","newinform":0,"love":0,"newmessage":0}
     * errorcode : 00000:ok
     */

    private ResultBean result;
    private String errorcode;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public static class ResultBean implements Parcelable {
        /**
         * uidx : 1628330494
         * showuidx : 1628330494
         * fromtype : 0
         * password :
         * nickName : 6L+H5oqK55i+5bCx5q27
         * gender : 1
         * signature :
         * headphoto :
         * aHR0cDovL3EucWxvZ28uY24vcXFhcHAvMTEwNTk5NTIwNS9CMDA1RDU4ODNERkEwQUREOENDMUIzRDlEMkVGNDhDQi8xMDA=
         * bgphoto :
         * aHR0cDovL3EucWxvZ28uY24vcXFhcHAvMTEwNTk5NTIwNS9CMDA1RDU4ODNERkEwQUREOENDMUIzRDlEMkVGNDhDQi8xMDA=
         * wallet : 0
         * province : 5rW35aSW
         * city : 576O5Zu9
         * phoneBrand : MA==
         * Binding : 0
         * source : 0
         * mobilePhone : MA==
         * states : MA==
         * lastLoginTime :
         * vNum : 0
         * newComment : 0
         * comment : 0
         * fansNum : 0
         * followNum : 0
         * newZanNum : 0
         * zanNum : 0
         * newtNum : 0
         * tNum : 0
         * longitude : 0
         * latitude : 0
         * userborn : 19850101
         * alipay :
         * alipayName :
         * newinform : 0
         * love : 0
         * newmessage : 0
         */

        private int uidx;
        private int showuidx;
        private int fromtype;
        private String password;
        private String nickName;
        private int gender;
        private String signature;
        private String headphoto;
        private String bgphoto;
        private double wallet;
        private String province;
        private String city;
        private String phoneBrand;
        private int Binding;
        private int source;
        private String mobilePhone;
        private String states;
        private String lastLoginTime;
        private int vNum;
        private int newComment;
        private int comment;
        private int fansNum;
        private int followNum;
        private int newZanNum;
        private int zanNum;
        private int newtNum;
        private int tNum;
        private double longitude;
        private double latitude;
        private String userborn;
        private String alipay;
        private String alipayName;
        private int newinform;
        private int love;
        private int newmessage;

        public int getUidx() {
            return uidx;
        }

        public void setUidx(int uidx) {
            this.uidx = uidx;
        }

        public int getShowuidx() {
            return showuidx;
        }

        public void setShowuidx(int showuidx) {
            this.showuidx = showuidx;
        }

        public int getFromtype() {
            return fromtype;
        }

        public void setFromtype(int fromtype) {
            this.fromtype = fromtype;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getHeadphoto() {
            return headphoto;
        }

        public void setHeadphoto(String headphoto) {
            this.headphoto = headphoto;
        }

        public String getBgphoto() {
            return bgphoto;
        }

        public void setBgphoto(String bgphoto) {
            this.bgphoto = bgphoto;
        }

        public double getWallet() {
            return wallet;
        }

        public void setWallet(int wallet) {
            this.wallet = wallet;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPhoneBrand() {
            return phoneBrand;
        }

        public void setPhoneBrand(String phoneBrand) {
            this.phoneBrand = phoneBrand;
        }

        public int getBinding() {
            return Binding;
        }

        public void setBinding(int Binding) {
            this.Binding = Binding;
        }

        public int getSource() {
            return source;
        }

        public void setSource(int source) {
            this.source = source;
        }

        public String getMobilePhone() {
            return mobilePhone;
        }

        public void setMobilePhone(String mobilePhone) {
            this.mobilePhone = mobilePhone;
        }

        public String getStates() {
            return states;
        }

        public void setStates(String states) {
            this.states = states;
        }

        public String getLastLoginTime() {
            return lastLoginTime;
        }

        public void setLastLoginTime(String lastLoginTime) {
            this.lastLoginTime = lastLoginTime;
        }

        public int getVNum() {
            return vNum;
        }

        public void setVNum(int vNum) {
            this.vNum = vNum;
        }

        public int getNewComment() {
            return newComment;
        }

        public void setNewComment(int newComment) {
            this.newComment = newComment;
        }

        public int getComment() {
            return comment;
        }

        public void setComment(int comment) {
            this.comment = comment;
        }

        public int getFansNum() {
            return fansNum;
        }

        public void setFansNum(int fansNum) {
            this.fansNum = fansNum;
        }

        public int getFollowNum() {
            return followNum;
        }

        public void setFollowNum(int followNum) {
            this.followNum = followNum;
        }

        public int getNewZanNum() {
            return newZanNum;
        }

        public void setNewZanNum(int newZanNum) {
            this.newZanNum = newZanNum;
        }

        public int getZanNum() {
            return zanNum;
        }

        public void setZanNum(int zanNum) {
            this.zanNum = zanNum;
        }

        public int getNewtNum() {
            return newtNum;
        }

        public void setNewtNum(int newtNum) {
            this.newtNum = newtNum;
        }

        public int getTNum() {
            return tNum;
        }

        public void setTNum(int tNum) {
            this.tNum = tNum;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(int longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(int latitude) {
            this.latitude = latitude;
        }

        public String getUserborn() {
            return userborn;
        }

        public void setUserborn(String userborn) {
            this.userborn = userborn;
        }

        public String getAlipay() {
            return alipay;
        }

        public void setAlipay(String alipay) {
            this.alipay = alipay;
        }

        public String getAlipayName() {
            return alipayName;
        }

        public void setAlipayName(String alipayName) {
            this.alipayName = alipayName;
        }

        public int getNewinform() {
            return newinform;
        }

        public void setNewinform(int newinform) {
            this.newinform = newinform;
        }

        public int getLove() {
            return love;
        }

        public void setLove(int love) {
            this.love = love;
        }

        public int getNewmessage() {
            return newmessage;
        }

        public void setNewmessage(int newmessage) {
            this.newmessage = newmessage;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.uidx);
            dest.writeInt(this.showuidx);
            dest.writeInt(this.fromtype);
            dest.writeString(this.password);
            dest.writeString(this.nickName);
            dest.writeInt(this.gender);
            dest.writeString(this.signature);
            dest.writeString(this.headphoto);
            dest.writeString(this.bgphoto);
            dest.writeDouble(this.wallet);
            dest.writeString(this.province);
            dest.writeString(this.city);
            dest.writeString(this.phoneBrand);
            dest.writeInt(this.Binding);
            dest.writeInt(this.source);
            dest.writeString(this.mobilePhone);
            dest.writeString(this.states);
            dest.writeString(this.lastLoginTime);
            dest.writeInt(this.vNum);
            dest.writeInt(this.newComment);
            dest.writeInt(this.comment);
            dest.writeInt(this.fansNum);
            dest.writeInt(this.followNum);
            dest.writeInt(this.newZanNum);
            dest.writeInt(this.zanNum);
            dest.writeInt(this.newtNum);
            dest.writeInt(this.tNum);
            dest.writeDouble(this.longitude);
            dest.writeDouble(this.latitude);
            dest.writeString(this.userborn);
            dest.writeString(this.alipay);
            dest.writeString(this.alipayName);
            dest.writeInt(this.newinform);
            dest.writeInt(this.love);
            dest.writeInt(this.newmessage);
        }

        public ResultBean() {
        }

        protected ResultBean(Parcel in) {
            this.uidx = in.readInt();
            this.showuidx = in.readInt();
            this.fromtype = in.readInt();
            this.password = in.readString();
            this.nickName = in.readString();
            this.gender = in.readInt();
            this.signature = in.readString();
            this.headphoto = in.readString();
            this.bgphoto = in.readString();
            this.wallet = in.readDouble();
            this.province = in.readString();
            this.city = in.readString();
            this.phoneBrand = in.readString();
            this.Binding = in.readInt();
            this.source = in.readInt();
            this.mobilePhone = in.readString();
            this.states = in.readString();
            this.lastLoginTime = in.readString();
            this.vNum = in.readInt();
            this.newComment = in.readInt();
            this.comment = in.readInt();
            this.fansNum = in.readInt();
            this.followNum = in.readInt();
            this.newZanNum = in.readInt();
            this.zanNum = in.readInt();
            this.newtNum = in.readInt();
            this.tNum = in.readInt();
            this.longitude = in.readDouble();
            this.latitude = in.readDouble();
            this.userborn = in.readString();
            this.alipay = in.readString();
            this.alipayName = in.readString();
            this.newinform = in.readInt();
            this.love = in.readInt();
            this.newmessage = in.readInt();
        }

        public static final Parcelable.Creator<ResultBean> CREATOR = new Parcelable.Creator<ResultBean>() {
            @Override
            public ResultBean createFromParcel(Parcel source) {
                return new ResultBean(source);
            }

            @Override
            public ResultBean[] newArray(int size) {
                return new ResultBean[size];
            }
        };
    }
}
