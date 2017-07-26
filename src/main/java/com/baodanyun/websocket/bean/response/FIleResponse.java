package com.baodanyun.websocket.bean.response;

/**
 * Created by yutao on 2015/9/4.
 */
public class FIleResponse {
    private String fileType;
    private String userType;
    private String id;
    private String date;
    private String childFileType;
    private String src;
    private String path;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getChildFileType() {
        return childFileType;
    }

    public void setChildFileType(String childFileType) {
        this.childFileType = childFileType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
