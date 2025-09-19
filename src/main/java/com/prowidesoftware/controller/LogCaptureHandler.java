package com.prowidesoftware.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class LogCaptureHandler extends Handler {
    private final List<String> warningAndSevereLogs = new ArrayList<>();

//    SEVERE (highest value)
//    WARNING
//    INFO
//    CONFIG
//    FINE
//    FINER
//    FINEST (lowest value)
    @Override
    public void publish(LogRecord record) {
        if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
            warningAndSevereLogs.add(record.getLevel() + ": " + record.getMessage());
        }
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}

    public List<String> getWarningAndSevereLogs() {
        return new ArrayList<>(warningAndSevereLogs);
    }

    public void clearLogs() {
        warningAndSevereLogs.clear();
    }
}
