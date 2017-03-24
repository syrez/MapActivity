package com.exmple.android.myapplication.Model;


/**
 * Created by k3vin on 21-03-17.
 */

public class ItemObjects {
    private String objectId;
    private String name;
    private int url;

    public ItemObjects(String name, int url) {
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

    public int getUrl() {
        return url;
    }

    public void setUrl(int url) {
        this.url = url;
    }

    public String getObjectId() {
        return objectId;
    }
}
