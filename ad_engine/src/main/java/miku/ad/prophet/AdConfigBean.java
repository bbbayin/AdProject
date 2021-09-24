/**
 * Copyright 2020 bejson.com
 */
package miku.ad.prophet;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class AdConfigBean implements Serializable {
    @SerializedName("name")
    private String name;
    @SerializedName("version")
    private int version;
    @SerializedName("refresh")
    private int refresh;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setRefresh(int refresh) {
        this.refresh = refresh;
    }

    public int getRefresh() {
        return refresh;
    }

}