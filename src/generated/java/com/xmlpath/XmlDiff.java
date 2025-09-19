package com.xmlpath;

public class XmlDiff {
    private String xpath;
    private String xml1Value;
    private String xml2Value;
    private boolean match;

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getXml1Value() {
        return xml1Value;
    }

    public void setXml1Value(String xml1Value) {
        this.xml1Value = xml1Value;
    }

    public String getXml2Value() {
        return xml2Value;
    }

    public void setXml2Value(String xml2Value) {
        this.xml2Value = xml2Value;
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }
}

