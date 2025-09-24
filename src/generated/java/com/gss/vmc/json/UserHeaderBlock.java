
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class UserHeaderBlock {

    @SerializedName("userHeaderBlockFields")
    @Expose
    private UserHeaderBlockFields userHeaderBlockFields;

    public UserHeaderBlockFields getUserHeaderBlockFields() {
        return userHeaderBlockFields;
    }

    public void setUserHeaderBlockFields(UserHeaderBlockFields userHeaderBlockFields) {
        this.userHeaderBlockFields = userHeaderBlockFields;
    }

}
