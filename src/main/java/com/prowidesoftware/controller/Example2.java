package com.prowidesoftware.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Example2 {
    public static void main(String[] args) {
        final String regex = "\\{(\\d{3}):([^\\}]+)\\}";
        final String string = "{3:{121:BARCGB2L}{121:BARCGB2TEST}{108:TEST1131stTime}{119:01}{103:SETTLEMENTINFO}{125:TX12345}{108:TEST1132ndTIME}}";
        
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(string);
        HashMap<String, String> keyvalue = new HashMap<String, String>();
        List<String> duplicateFields = new ArrayList<>();
        while(matcher.find()) {
        	if(!keyvalue.containsKey(matcher.group(1))) {
        		keyvalue.put(matcher.group(1), matcher.group(2));
        	}
        	else {
        		duplicateFields.add("❌ Duplicate Field/Tag found at Block-3 >\t"+matcher.group(0));
        	}
        }
        duplicateFields.forEach(System.out::println);
    }
}
