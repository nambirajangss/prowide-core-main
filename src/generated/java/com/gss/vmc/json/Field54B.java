
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Field54B {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("dCMark")
    @Expose
    private String dCMark;
    @SerializedName("account")
    @Expose
    private String account;
    @SerializedName("accIndicator")
    @Expose
    private String accIndicator;
    @SerializedName("location")
    @Expose
    private String location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getdCMark() {
        return dCMark;
    }

    public void setdCMark(String dCMark) {
        this.dCMark = dCMark;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccIndicator() {
        return accIndicator;
    }

    public void setAccIndicator(String accIndicator) {
        this.accIndicator = accIndicator;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
