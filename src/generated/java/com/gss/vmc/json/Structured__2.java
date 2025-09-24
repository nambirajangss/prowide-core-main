
package com.gss.vmc.json;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Structured__2 {

    @SerializedName("narrativeFragments")
    @Expose
    private List<String> narrativeFragments;
    @SerializedName("narrativeSupplementFragments")
    @Expose
    private List<Object> narrativeSupplementFragments;
    @SerializedName("codeword")
    @Expose
    private String codeword;

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

}
