package com.l.eyescure.server.printManager;

/**
 * Created by Administrator on 2017/9/5.
 */
public class PrintUnit {
    private int left;
    private int top;
    private int right;
    private int bottom;
    private String quality = "Standard";
    private String size = "A4";
    private String path = "LANDSCAPE";

    public PrintUnit(int left, int top, int right, int bottom, String quality, String size, String path) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.quality = quality;
        this.size = size;
        this.path = path;
    }

    public PrintUnit(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
