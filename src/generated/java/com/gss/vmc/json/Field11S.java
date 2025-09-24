
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Field11S {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mTNumber")
    @Expose
    private String mTNumber;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("sessionNumber")
    @Expose
    private String sessionNumber;
    @SerializedName("iSN")
    @Expose
    private String iSN;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getmTNumber() {
        return mTNumber;
    }

    public void setmTNumber(String mTNumber) {
        this.mTNumber = mTNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(String sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public String getiSN() {
        return iSN;
    }

    public void setiSN(String iSN) {
        this.iSN = iSN;
    }

}
