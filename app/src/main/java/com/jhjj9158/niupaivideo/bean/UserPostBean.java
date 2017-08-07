package com.jhjj9158.niupaivideo.bean;

/**
 * Created by pc on 17-4-12.
 */

public class UserPostBean {

    private String opcode;
    private int useridx;
    private String name;
    private int chooseSelect;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getChooseSelect() {
        return chooseSelect;
    }

    public void setChooseSelect(int chooseSelect) {
        this.chooseSelect = chooseSelect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
