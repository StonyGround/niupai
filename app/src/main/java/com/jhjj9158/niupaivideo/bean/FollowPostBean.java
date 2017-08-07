package com.jhjj9158.niupaivideo.bean;

/**
 * Created by pc on 17-4-12.
 */

public class FollowPostBean {

    private String opcode;
    private int useridx;
    private int friendidx;
    private int index;

    public int getFriendidx() {
        return friendidx;
    }

    public void setFriendidx(int friendidx) {
        this.friendidx = friendidx;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getUseridx() {
        return useridx;
    }

    public void setUseridx(int useridx) {
        this.useridx = useridx;
    }

    public String getOpcode() {
        return opcode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }
}
