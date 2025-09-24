
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Field103 {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("fINCopyServiceCode")
    @Expose
    private String fINCopyServiceCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getfINCopyServiceCode() {
        return fINCopyServiceCode;
    }

    public void setfINCopyServiceCode(String fINCopyServiceCode) {
        this.fINCopyServiceCode = fINCopyServiceCode;
    }

}
