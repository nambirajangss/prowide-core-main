
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Field56C__1 {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("account")
    @Expose
    private String account;
    @SerializedName("accIndicator")
    @Expose
    private String accIndicator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}
