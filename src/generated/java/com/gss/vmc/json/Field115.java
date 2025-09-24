
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Field115 {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("paymentReleaseInformationReceiver")
    @Expose
    private String paymentReleaseInformationReceiver;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPaymentReleaseInformationReceiver() {
        return paymentReleaseInformationReceiver;
    }

    public void setPaymentReleaseInformationReceiver(String paymentReleaseInformationReceiver) {
        this.paymentReleaseInformationReceiver = paymentReleaseInformationReceiver;
    }

}
