
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class RepeatField13C__1 {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("timeIndication")
    @Expose
    private String timeIndication;
    @SerializedName("sign")
    @Expose
    private String sign;
    @SerializedName("timeOffset")
    @Expose
    private String timeOffset;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTimeIndication() {
        return timeIndication;
    }

    public void setTimeIndication(String timeIndication) {
        this.timeIndication = timeIndication;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimeOffset() {
        return timeOffset;
    }

    public void setTimeOffset(String timeOffset) {
        this.timeOffset = timeOffset;
    }

}
