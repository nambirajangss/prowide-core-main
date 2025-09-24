
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class ApplicationHeaderBlock {

    @SerializedName("senderInputTime")
    @Expose
    private String senderInputTime;
    @SerializedName("MIRDate")
    @Expose
    private String mIRDate;
    @SerializedName("MIRLogicalTerminal")
    @Expose
    private String mIRLogicalTerminal;
    @SerializedName("MIRSessionNumber")
    @Expose
    private String mIRSessionNumber;
    @SerializedName("MIRSequenceNumber")
    @Expose
    private String mIRSequenceNumber;
    @SerializedName("receiverOutputDate")
    @Expose
    private String receiverOutputDate;
    @SerializedName("receiverOutputTime")
    @Expose
    private String receiverOutputTime;
    @SerializedName("messagePriority")
    @Expose
    private String messagePriority;
    @SerializedName("messageType")
    @Expose
    private String messageType;
    @SerializedName("blockType")
    @Expose
    private String blockType;
    @SerializedName("direction")
    @Expose
    private String direction;

    public String getSenderInputTime() {
        return senderInputTime;
    }

    public void setSenderInputTime(String senderInputTime) {
        this.senderInputTime = senderInputTime;
    }

    public String getMIRDate() {
        return mIRDate;
    }

    public void setMIRDate(String mIRDate) {
        this.mIRDate = mIRDate;
    }

    public String getMIRLogicalTerminal() {
        return mIRLogicalTerminal;
    }

    public void setMIRLogicalTerminal(String mIRLogicalTerminal) {
        this.mIRLogicalTerminal = mIRLogicalTerminal;
    }

    public String getMIRSessionNumber() {
        return mIRSessionNumber;
    }

    public void setMIRSessionNumber(String mIRSessionNumber) {
        this.mIRSessionNumber = mIRSessionNumber;
    }

    public String getMIRSequenceNumber() {
        return mIRSequenceNumber;
    }

    public void setMIRSequenceNumber(String mIRSequenceNumber) {
        this.mIRSequenceNumber = mIRSequenceNumber;
    }

    public String getReceiverOutputDate() {
        return receiverOutputDate;
    }

    public void setReceiverOutputDate(String receiverOutputDate) {
        this.receiverOutputDate = receiverOutputDate;
    }

    public String getReceiverOutputTime() {
        return receiverOutputTime;
    }

    public void setReceiverOutputTime(String receiverOutputTime) {
        this.receiverOutputTime = receiverOutputTime;
    }

    public String getMessagePriority() {
        return messagePriority;
    }

    public void setMessagePriority(String messagePriority) {
        this.messagePriority = messagePriority;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getBlockType() {
        return blockType;
    }

    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

}
