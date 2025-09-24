
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class BasicHeaderBlock {

    @SerializedName("applicationId")
    @Expose
    private String applicationId;
    @SerializedName("serviceId")
    @Expose
    private String serviceId;
    @SerializedName("logicalTerminal")
    @Expose
    private String logicalTerminal;
    @SerializedName("sessionNumber")
    @Expose
    private String sessionNumber;
    @SerializedName("sequenceNumber")
    @Expose
    private String sequenceNumber;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getLogicalTerminal() {
        return logicalTerminal;
    }

    public void setLogicalTerminal(String logicalTerminal) {
        this.logicalTerminal = logicalTerminal;
    }

    public String getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(String sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

}
