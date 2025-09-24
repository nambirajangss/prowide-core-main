
package com.gss.vmc.json;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Structured__4 {

    @SerializedName("narrativeFragments")
    @Expose
    private List<String> narrativeFragments;
    @SerializedName("narrativeSupplementFragments")
    @Expose
    private List<Object> narrativeSupplementFragments;
    @SerializedName("codeword")
    @Expose
    private String codeword;
    @SerializedName("mtName")
    @Expose
    private String mtName;
    @SerializedName("mtCountry")
    @Expose
    private String mtCountry;
    @SerializedName("mtTownName")
    @Expose
    private String mtTownName;
    @SerializedName("mtBIC")
    @Expose
    private String mtBIC;
    @SerializedName("mtOther")
    @Expose
    private String mtOther;
    @SerializedName("mtCode")
    @Expose
    private String mtCode;
    @SerializedName("mtMemberId")
    @Expose
    private String mtMemberId;

    public List<String> getNarrativeFragments() {
        return narrativeFragments;
    }

    public void setNarrativeFragments(List<String> narrativeFragments) {
        this.narrativeFragments = narrativeFragments;
    }

    public List<Object> getNarrativeSupplementFragments() {
        return narrativeSupplementFragments;
    }

    public void setNarrativeSupplementFragments(List<Object> narrativeSupplementFragments) {
        this.narrativeSupplementFragments = narrativeSupplementFragments;
    }

    public String getCodeword() {
        return codeword;
    }

    public void setCodeword(String codeword) {
        this.codeword = codeword;
    }

    public String getMtName() {
        return mtName;
    }

    public void setMtName(String mtName) {
        this.mtName = mtName;
    }

    public String getMtCountry() {
        return mtCountry;
    }

    public void setMtCountry(String mtCountry) {
        this.mtCountry = mtCountry;
    }

    public String getMtTownName() {
        return mtTownName;
    }

    public void setMtTownName(String mtTownName) {
        this.mtTownName = mtTownName;
    }

    public String getMtBIC() {
        return mtBIC;
    }

    public void setMtBIC(String mtBIC) {
        this.mtBIC = mtBIC;
    }

    public String getMtOther() {
        return mtOther;
    }

    public void setMtOther(String mtOther) {
        this.mtOther = mtOther;
    }

    public String getMtCode() {
        return mtCode;
    }

    public void setMtCode(String mtCode) {
        this.mtCode = mtCode;
    }

    public String getMtMemberId() {
        return mtMemberId;
    }

    public void setMtMemberId(String mtMemberId) {
        this.mtMemberId = mtMemberId;
    }

}
