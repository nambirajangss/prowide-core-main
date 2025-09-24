
package com.gss.vmc.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class TextBlock {

    @SerializedName("textBlockFields")
    @Expose
    private TextBlockFields textBlockFields;

    public TextBlockFields getTextBlockFields() {
        return textBlockFields;
    }

    public void setTextBlockFields(TextBlockFields textBlockFields) {
        this.textBlockFields = textBlockFields;
    }

}
