
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Field433 {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("screeningResults")
    @Expose
    private String screeningResults;
    @SerializedName("additionalInformation")
    @Expose
    private String additionalInformation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreeningResults() {
        return screeningResults;
    }

    public void setScreeningResults(String screeningResults) {
        this.screeningResults = screeningResults;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

}
