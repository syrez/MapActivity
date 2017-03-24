package com.exmple.android.myapplication.Model;

/**
 * Created by k3vin on 20-03-17.
 */

public class Artwork {
    private String objectId;
    private String name;
    private String url;

    public Artwork(String name, String url, String objectId) {
        this.name = name;
        this.url = url;
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getObjectId() {
        return objectId;
    }

}
