
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class TrailerBlockFields {

    @SerializedName("fieldMAC")
    @Expose
    private FieldMAC fieldMAC;
    @SerializedName("fieldPAC")
    @Expose
    private FieldPAC fieldPAC;
    @SerializedName("fieldCHK")
    @Expose
    private FieldCHK fieldCHK;
    @SerializedName("fieldTNG")
    @Expose
    private FieldTNG fieldTNG;
    @SerializedName("fieldPDE")
    @Expose
    private FieldPDE fieldPDE;
    @SerializedName("fieldSYS")
    @Expose
    private FieldSYS fieldSYS;
    @SerializedName("fieldPDM")
    @Expose
    private FieldPDM fieldPDM;
    @SerializedName("fieldDLM")
    @Expose
    private FieldDLM fieldDLM;
    @SerializedName("fieldMRF")
    @Expose
    private FieldMRF fieldMRF;

    public FieldMAC getFieldMAC() {
        return fieldMAC;
    }

    public void setFieldMAC(FieldMAC fieldMAC) {
        this.fieldMAC = fieldMAC;
    }

    public FieldPAC getFieldPAC() {
        return fieldPAC;
    }

    public void setFieldPAC(FieldPAC fieldPAC) {
        this.fieldPAC = fieldPAC;
    }

    public FieldCHK getFieldCHK() {
        return fieldCHK;
    }

    public void setFieldCHK(FieldCHK fieldCHK) {
        this.fieldCHK = fieldCHK;
    }

    public FieldTNG getFieldTNG() {
        return fieldTNG;
    }

    public void setFieldTNG(FieldTNG fieldTNG) {
        this.fieldTNG = fieldTNG;
    }

    public FieldPDE getFieldPDE() {
        return fieldPDE;
    }

    public void setFieldPDE(FieldPDE fieldPDE) {
        this.fieldPDE = fieldPDE;
    }

    public FieldSYS getFieldSYS() {
        return fieldSYS;
    }

    public void setFieldSYS(FieldSYS fieldSYS) {
        this.fieldSYS = fieldSYS;
    }

    public FieldPDM getFieldPDM() {
        return fieldPDM;
    }

    public void setFieldPDM(FieldPDM fieldPDM) {
        this.fieldPDM = fieldPDM;
    }

    public FieldDLM getFieldDLM() {
        return fieldDLM;
    }

    public void setFieldDLM(FieldDLM fieldDLM) {
        this.fieldDLM = fieldDLM;
    }

    public FieldMRF getFieldMRF() {
        return fieldMRF;
    }

    public void setFieldMRF(FieldMRF fieldMRF) {
        this.fieldMRF = fieldMRF;
    }

}
