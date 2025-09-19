package com.xmlpath;

import java.util.List;

public class TestResult {
    private String fileName;
    private List<XmlDiff> diffs;
    private int passPercent;
    private int errorCount;

    // Constructor
    public TestResult() {}

    public TestResult(String fileName, List<XmlDiff> diffs, int passPercent, int errorCount) {
        this.fileName = fileName;
        this.diffs = diffs;
        this.passPercent = passPercent;
        this.errorCount = errorCount;
    }

    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<XmlDiff> getDiffs() {
        return diffs;
    }

    public void setDiffs(List<XmlDiff> diffs) {
        this.diffs = diffs;
    }

    public int getPassPercent() {
        return passPercent;
    }

    public void setPassPercent(int passPercent) {
        this.passPercent = passPercent;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
}
