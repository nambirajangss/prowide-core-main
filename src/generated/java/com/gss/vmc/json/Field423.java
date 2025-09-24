
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Field423 {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("balanceCheckpointDateTime")
    @Expose
    private String balanceCheckpointDateTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBalanceCheckpointDateTime() {
        return balanceCheckpointDateTime;
    }

    public void setBalanceCheckpointDateTime(String balanceCheckpointDateTime) {
        this.balanceCheckpointDateTime = balanceCheckpointDateTime;
    }

}
