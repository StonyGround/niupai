package com.xiuxiu.model;

import java.util.List;

/**
 * Created by hzdykj on 2017/7/3.
 */

public class ThemeTypeInfo {

    /**
     * name : shy
     * type : 2
     * width : 120
     * height : 120
     * duration : 2.5
     * category : run
     * frames : [{"time":0.1,"pic":1},{"time":0.2,"pic":2},{"time":0.3,"pic":3},{"time":0.4,"pic":4},{"time":0.5,"pic":5},{"time":0.6,"pic":6},{"time":0.7,"pic":7},{"time":0.8,"pic":8},{"time":0.9,"pic":9},{"time":2.5,"pic":9}]
     */

    private String name;
    private int type;
    private int width;
    private int height;
    private double duration;
    private String category;
    private List<FramesBean> frames;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<FramesBean> getFrames() {
        return frames;
    }

    public void setFrames(List<FramesBean> frames) {
        this.frames = frames;
    }

    public static class FramesBean {
        /**
         * time : 0.1
         * pic : 1
         */

        private double time;
        private int pic;

        public double getTime() {
            return time;
        }

        public void setTime(double time) {
            this.time = time;
        }

        public int getPic() {
            return pic;
        }

        public void setPic(int pic) {
            this.pic = pic;
        }
    }
}
