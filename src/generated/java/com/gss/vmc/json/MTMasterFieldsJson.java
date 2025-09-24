
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class MTMasterFieldsJson {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("basicHeaderBlock")
    @Expose
    private BasicHeaderBlock basicHeaderBlock;
    @SerializedName("applicationHeaderBlock")
    @Expose
    private ApplicationHeaderBlock applicationHeaderBlock;
    @SerializedName("userHeaderBlock")
    @Expose
    private UserHeaderBlock userHeaderBlock;
    @SerializedName("textBlock")
    @Expose
    private TextBlock textBlock;
    @SerializedName("trailerBlock")
    @Expose
    private TrailerBlock trailerBlock;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BasicHeaderBlock getBasicHeaderBlock() {
        return basicHeaderBlock;
    }

    public void setBasicHeaderBlock(BasicHeaderBlock basicHeaderBlock) {
        this.basicHeaderBlock = basicHeaderBlock;
    }

    public ApplicationHeaderBlock getApplicationHeaderBlock() {
        return applicationHeaderBlock;
    }

    public void setApplicationHeaderBlock(ApplicationHeaderBlock applicationHeaderBlock) {
        this.applicationHeaderBlock = applicationHeaderBlock;
    }

    public UserHeaderBlock getUserHeaderBlock() {
        return userHeaderBlock;
    }

    public void setUserHeaderBlock(UserHeaderBlock userHeaderBlock) {
        this.userHeaderBlock = userHeaderBlock;
    }

    public TextBlock getTextBlock() {
        return textBlock;
    }

    public void setTextBlock(TextBlock textBlock) {
        this.textBlock = textBlock;
    }

    public TrailerBlock getTrailerBlock() {
        return trailerBlock;
    }

    public void setTrailerBlock(TrailerBlock trailerBlock) {
        this.trailerBlock = trailerBlock;
    }

}
