
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Field106 {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mIR")
    @Expose
    private String mIR;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getmIR() {
        return mIR;
    }

    public void setmIR(String mIR) {
        this.mIR = mIR;
    }

}
