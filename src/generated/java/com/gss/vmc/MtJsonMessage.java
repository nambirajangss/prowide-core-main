package com.gss.vmc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gss.vmc.MtJsonMessage.*;
import com.prowidesoftware.swift.model.field.Field;

import java.util.HashMap;
import java.util.Map;

/**
 * POJO representing an MT message structure, mirroring the JSON schema.
 */
public class MtJsonMessage {
	
	public MtJsonMessage(){
		this.basicHeaderBlock = new BasicHeaderBlock();
		this.applicationHeaderBlock = new ApplicationHeaderBlock();
		this.userHeaderBlock = new UserHeaderBlock();
		this.textBlock = new TextBlock();
		this.trailerBlock = new TrailerBlock();
	}

	public BasicHeaderBlock basicHeaderBlock = new BasicHeaderBlock();
	public ApplicationHeaderBlock applicationHeaderBlock = new ApplicationHeaderBlock();
	public UserHeaderBlock userHeaderBlock = new UserHeaderBlock();
	public TextBlock textBlock = new TextBlock();
	public TrailerBlock trailerBlock = new TrailerBlock();

    // Getters and setters
    public BasicHeaderBlock getBasicHeaderBlock() { return basicHeaderBlock; }
    public ApplicationHeaderBlock getApplicationHeaderBlock() { return applicationHeaderBlock; }
    public UserHeaderBlock getUserHeaderBlock() { return userHeaderBlock; }
    public TextBlock getTextBlock() { return textBlock; }
    public TrailerBlock getTrailerBlock() { return trailerBlock; }

    // Inner classes for blocks
    public static class BasicHeaderBlock {
        private String applicationId;
        private String serviceId;
        private String logicalTerminal;
        private String sessionNumber;
        private String sequenceNumber;

        // Default values
        private static final String DEFAULT_APPLICATION_ID = "F";
        private static final String DEFAULT_SERVICE_ID = "01";
        private static final String DEFAULT_LOGICAL_TERMINAL = "XXXXXXYYYY";
        private static final String DEFAULT_SESSION_NUMBER = "0000";
        private static final String DEFAULT_SEQUENCE_NUMBER = "000000";

        // Getters with default values
        public String getApplicationId() {
            return applicationId != null ? applicationId : DEFAULT_APPLICATION_ID;
        }

        public String getServiceId() {
            return serviceId != null ? serviceId : DEFAULT_SERVICE_ID;
        }

        public String getLogicalTerminal() {
            return logicalTerminal != null ? logicalTerminal : DEFAULT_LOGICAL_TERMINAL;
        }

        public String getSessionNumber() {
            return sessionNumber != null ? sessionNumber : DEFAULT_SESSION_NUMBER;
        }

        public String getSequenceNumber() {
            return sequenceNumber != null ? sequenceNumber : DEFAULT_SEQUENCE_NUMBER;
        }

        // Setters
        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }

        public void setLogicalTerminal(String logicalTerminal) {
            this.logicalTerminal = logicalTerminal;
        }

        public void setSessionNumber(String sessionNumber) {
            this.sessionNumber = sessionNumber;
        }

        public void setSequenceNumber(String sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }
        
        // ✅ toJson method
        public String toJson() {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting() // optional, makes JSON formatted nicely
                    .create();
            return gson.toJson(this);
        }
        
        // ✅ Return JsonElement instead of String
        public JsonElement toJsonElement() {
        	 JsonObject jsonObject = new JsonObject();
        	    jsonObject.addProperty("applicationId", getApplicationId());
        	    jsonObject.addProperty("serviceId", getServiceId());
        	    jsonObject.addProperty("logicalTerminal", getLogicalTerminal());
        	    jsonObject.addProperty("sessionNumber", getSessionNumber());
        	    jsonObject.addProperty("sequenceNumber", getSequenceNumber());
        	    return jsonObject; // already a JsonElement
            //Gson gson = new GsonBuilder().create();
            //return gson.toJsonTree(jsonObject);
        }        
    }

    public static class ApplicationHeaderBlock {
        private String senderInputTime;
        private String MIRDate;
        private String MIRLogicalTerminal;
        private String MIRSessionNumber;
        private String MIRSequenceNumber;
        private String receiverOutputDate;
        private String receiverOutputTime;
        private String messagePriority;
        private String messageType;
        private String blockType;
        private String direction;

        // Default values
        private static final String DEFAULT_SENDER_INPUT_TIME = "0000";
        private static final String DEFAULT_MIR_DATE = "000000";
        private static final String DEFAULT_MIR_LOGICAL_TERMINAL = "XXXXXXYYYY";
        private static final String DEFAULT_MIR_SESSION_NUMBER = "0000";
        private static final String DEFAULT_MIR_SEQUENCE_NUMBER = "000000";
        private static final String DEFAULT_RECEIVER_OUTPUT_DATE = "000000";
        private static final String DEFAULT_RECEIVER_OUTPUT_TIME = "0000";
        private static final String DEFAULT_MESSAGE_PRIORITY = "N";
        private static final String DEFAULT_MESSAGE_TYPE = "000";
        private static final String DEFAULT_BLOCK_TYPE = "2";
        private static final String DEFAULT_DIRECTION = "I";

        // Getters with defaults
        public String getSenderInputTime() {
            return senderInputTime != null ? senderInputTime : DEFAULT_SENDER_INPUT_TIME;
        }

        public String getMIRDate() {
            return MIRDate != null ? MIRDate : DEFAULT_MIR_DATE;
        }

        public String getMIRLogicalTerminal() {
            return MIRLogicalTerminal != null ? MIRLogicalTerminal : DEFAULT_MIR_LOGICAL_TERMINAL;
        }

        public String getMIRSessionNumber() {
            return MIRSessionNumber != null ? MIRSessionNumber : DEFAULT_MIR_SESSION_NUMBER;
        }

        public String getMIRSequenceNumber() {
            return MIRSequenceNumber != null ? MIRSequenceNumber : DEFAULT_MIR_SEQUENCE_NUMBER;
        }

        public String getReceiverOutputDate() {
            return receiverOutputDate != null ? receiverOutputDate : DEFAULT_RECEIVER_OUTPUT_DATE;
        }

        public String getReceiverOutputTime() {
            return receiverOutputTime != null ? receiverOutputTime : DEFAULT_RECEIVER_OUTPUT_TIME;
        }

        public String getMessagePriority() {
            return messagePriority != null ? messagePriority : DEFAULT_MESSAGE_PRIORITY;
        }

        public String getMessageType() {
            return messageType != null ? messageType : DEFAULT_MESSAGE_TYPE;
        }

        public String getBlockType() {
            return blockType != null ? blockType : DEFAULT_BLOCK_TYPE;
        }

        public String getDirection() {
            return direction != null ? direction : DEFAULT_DIRECTION;
        }

        // Setters
        public void setSenderInputTime(String senderInputTime) {
            this.senderInputTime = senderInputTime;
        }

        public void setMIRDate(String MIRDate) {
            this.MIRDate = MIRDate;
        }

        public void setMIRLogicalTerminal(String MIRLogicalTerminal) {
            this.MIRLogicalTerminal = MIRLogicalTerminal;
        }

        public void setMIRSessionNumber(String MIRSessionNumber) {
            this.MIRSessionNumber = MIRSessionNumber;
        }

        public void setMIRSequenceNumber(String MIRSequenceNumber) {
            this.MIRSequenceNumber = MIRSequenceNumber;
        }

        public void setReceiverOutputDate(String receiverOutputDate) {
            this.receiverOutputDate = receiverOutputDate;
        }

        public void setReceiverOutputTime(String receiverOutputTime) {
            this.receiverOutputTime = receiverOutputTime;
        }

        public void setMessagePriority(String messagePriority) {
            this.messagePriority = messagePriority;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }

        public void setBlockType(String blockType) {
            this.blockType = blockType;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }
        // ✅ toJson method
        public String toJson() {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting() // optional, makes JSON formatted nicely
                    .create();
            return gson.toJson(this);
        }
        
        // ✅ Return JsonElement instead of String
        public JsonElement toJsonElement() {
        	JsonObject jsonObject = new JsonObject();
    	    jsonObject.addProperty("senderInputTime", getSenderInputTime());
    	    jsonObject.addProperty("MIRDate", getMIRDate());
    	    jsonObject.addProperty("MIRLogicalTerminal", getMIRLogicalTerminal());
    	    jsonObject.addProperty("MIRSessionNumber", getMIRSessionNumber());
    	    jsonObject.addProperty("MIRSequenceNumber", getMIRSequenceNumber());
    	    jsonObject.addProperty("receiverOutputDate", getReceiverOutputDate());
    	    jsonObject.addProperty("receiverOutputTime", getReceiverOutputTime());
    	    jsonObject.addProperty("messagePriority", getMessagePriority());
    	    jsonObject.addProperty("messageType", getMessageType());
    	    jsonObject.addProperty("blockType", getBlockType());
    	    jsonObject.addProperty("direction", getDirection());
    	    return jsonObject; // already a JsonElement
            //Gson gson = new GsonBuilder().create();
            //return gson.toJsonTree(this);
        }
    }

    public static class UserHeaderBlock {
        private Map<String, MtField> userHeaderBlockFields = new HashMap<>();

        // Default
        private static final Map<String, MtField> DEFAULT_USER_HEADER_BLOCK = new HashMap<>();

        // Getter
        public Map<String, MtField> getUserHeaderBlockFields() {
            return userHeaderBlockFields != null ? userHeaderBlockFields : DEFAULT_USER_HEADER_BLOCK;
        }

        // Setter
        public void setUserHeaderBlockFields(Map<String, MtField> userHeaderBlockFields) {
            this.userHeaderBlockFields = userHeaderBlockFields;
        }
        // ✅ toJson method
        public String toJson() {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting() // optional, makes JSON formatted nicely
                    .create();
            return gson.toJson(this);
        }
        
        // ✅ Return JsonElement instead of String
        public JsonElement toJsonElement() {        	
            Gson gson = new GsonBuilder().create();
            return gson.toJsonTree(this);
        }
    }

    public static class TextBlock {
        private Map<String, JsonObject> sequenceA = new HashMap<>(); // e.g., field20, field70

        // Getter with default
        public Map<String, JsonObject> getSequenceA() {
            if (sequenceA == null) {
                sequenceA = new HashMap<>(); // default empty map
            }
            return sequenceA;
        }

        // Setter
        public void setSequenceA(Map<String, JsonObject> sequenceA) {
            if (sequenceA == null) {
                this.sequenceA = new HashMap<>(); // ensure default map if null is passed
            } else {
                this.sequenceA = sequenceA;
            }
        }
    }

    public static class TrailerBlock {
        private Map<String, MtField> trailerBlockFields = new HashMap<>();

        // Getter with default
        public Map<String, MtField> getTrailerBlockFields() {
            if (trailerBlockFields == null) {
                trailerBlockFields = new HashMap<>(); // default empty map
            }
            return trailerBlockFields;
        }

        // Setter
        public void setTrailerBlockFields(Map<String, MtField> trailerBlockFields) {
            if (trailerBlockFields == null) {
                this.trailerBlockFields = new HashMap<>();
            } else {
                this.trailerBlockFields = trailerBlockFields;
            }
        }
    }

    // Dummy MtField class
    public static class MtField {
	    private String name;
	    private String value;
	
	    public MtField(String name, String value) {
	        this.name = name;
	        this.value = value;
	    }
	
	    // Getters/Setters
	    public String getName() {
	        return name;
	    }
	
	    public void setName(String name) {
	        this.name = name;
	    }
	
	    public String getValue() {
	        return value;
	    }
	
	    public void setValue(String value) {
	        this.value = value;
	    }
	}
}