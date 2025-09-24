
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class TrailerBlock {

    @SerializedName("trailerBlockFields")
    @Expose
    private TrailerBlockFields trailerBlockFields;

    public TrailerBlockFields getTrailerBlockFields() {
        return trailerBlockFields;
    }

    public void setTrailerBlockFields(TrailerBlockFields trailerBlockFields) {
        this.trailerBlockFields = trailerBlockFields;
    }

}
