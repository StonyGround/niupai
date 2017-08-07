package com.jhjj9158.niupaivideo.bean;

/**
 * Created by pc on 17-4-5.
 */

public class UserBean {

    private String opcode;
    private String useridx;
    private String password;
    private int platformtype;

    public String getUseridx() {
        return useridx;
    }

    public void setUseridx(String useridx) {
        this.useridx = useridx;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPlatformtype() {
        return platformtype;
    }

    public void setPlatformtype(int platformtype) {
        this.platformtype = platformtype;
    }

    public String getOpcode() {
        return opcode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }
}
