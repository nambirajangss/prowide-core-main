
package com.gss.vmc.json;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Field72__1 {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("narrative")
    @Expose
    private String narrative;
    @SerializedName("structured")
    @Expose
    private List<Structured__4> structured;
    @SerializedName("unstructuredFragments")
    @Expose
    private List<Object> unstructuredFragments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public List<Structured__4> getStructured() {
        return structured;
    }

    public void setStructured(List<Structured__4> structured) {
        this.structured = structured;
    }

    public List<Object> getUnstructuredFragments() {
        return unstructuredFragments;
    }

    public void setUnstructuredFragments(List<Object> unstructuredFragments) {
        this.unstructuredFragments = unstructuredFragments;
    }

}
