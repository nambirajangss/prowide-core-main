
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Field113 {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("bankingPriority")
    @Expose
    private String bankingPriority;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBankingPriority() {
        return bankingPriority;
    }

    public void setBankingPriority(String bankingPriority) {
        this.bankingPriority = bankingPriority;
    }

}
