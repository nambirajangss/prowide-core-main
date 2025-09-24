
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Field108 {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mUR")
    @Expose
    private String mUR;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getmUR() {
        return mUR;
    }

    public void setmUR(String mUR) {
        this.mUR = mUR;
    }

}
