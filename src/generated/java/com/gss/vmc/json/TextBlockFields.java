
package com.gss.vmc.json;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class TextBlockFields {

    @SerializedName("sequenceA")
    @Expose
    private SequenceA sequenceA;
    @SerializedName("sequenceB")
    @Expose
    private SequenceB sequenceB;
    @SerializedName("sequenceBList")
    @Expose
    private List<SequenceB__1> sequenceBList;
    @SerializedName("sequenceC")
    @Expose
    private SequenceC sequenceC;

    public SequenceA getSequenceA() {
        return sequenceA;
    }

    public void setSequenceA(SequenceA sequenceA) {
        this.sequenceA = sequenceA;
    }

    public SequenceB getSequenceB() {
        return sequenceB;
    }

    public void setSequenceB(SequenceB sequenceB) {
        this.sequenceB = sequenceB;
    }

    public List<SequenceB__1> getSequenceBList() {
        return sequenceBList;
    }

    public void setSequenceBList(List<SequenceB__1> sequenceBList) {
        this.sequenceBList = sequenceBList;
    }

    public SequenceC getSequenceC() {
        return sequenceC;
    }

    public void setSequenceC(SequenceC sequenceC) {
        this.sequenceC = sequenceC;
    }

}
