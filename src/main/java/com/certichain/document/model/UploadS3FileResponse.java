package com.certichain.document.model;

public class UploadS3FileResponse {

    private String path;
    private String hash;
    
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getHash() {
        return hash;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }
    
}
