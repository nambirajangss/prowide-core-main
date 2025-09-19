package com.prowidesoftware.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Example1 {
    public static void main(String[] args) {
        final String regex = "\\{4:\\s*(\\n:\\d{2}[A-Z]?:[^\\r]+(\\n[^\\r]*)*)+-\\}";
        final String regexBlock4 = "(?s)\\{4:\\s*(.*?)\\s*-\\}";
        final String childRegex = "(?::\\d{2}[A-Z]?:[^\\r\\n]*)(?:\\r?\\n(?!:).*)*";
        final String string = "{4:\n"
	 + ":20:REFERENCE12345\n"
	 + ":23a:CREDIT\n"
	 + ":51AA:BANKUSXX\n"
	 + ":50A:/123456789\n"
	 + "BANKUSXX\n"
	 + "hshsh\n"
	 + ":50FA:/789456123\n"
	 + "1ST STREET\n"
	 + "NEW YORK, US\n"
	 + "5:/852741963\n"
	 + "Company ABC\n"
	 + "456 MAIN ROAD\n"
	 + "LOS ANGELES, US\n"
	 + ":52a:/654987321\n"
	 + "BANKGB2LXXX\n"
	 + ":52B/C/852963741\n"
	 + "BANK NAME\n"
	 + "LONDON, GB\n"
	 + "52C:/159753852\n"
	 + "BANK NAME\n"
	 + "DUBAI, AE\n"
	 + ":26T12345XYZ\n"
	 + ":77B :/INS/Instruction details\n"
	 + ": 71A:SHA\n"
	 + ":36:1,15\n"
	 + " \n\n"
	 + ":21:PREVREF123\n"
	 + ":32B:USD2500,00\n"
	 + ":50A:/321456987\n"
	 + "BANKCNBJXXX\n"
	 + ":50F:/159632478\n"
	 + "Company XYZ\n"
	 + "Hong Kong\n"
	 + ":50K:/741258963\n"
	 + "Business Ltd\n"
	 + "Tokyo, JP\n"
	 + ":52A:/741369258\n"
	 + "BANKJPJTXXX\n"
	 + ":52B:/D/159357852\n"
	 + "BANK NAME\n"
	 + "SINGAPORE, SG\n"
	 + ":52C:/951753456\n"
	 + "BANK NAME\n"
	 + "SYDNEY, AU\n"
	 + ":57A:/741258963\n"
	 + "BANKAU2SXXX\n"
	 + ":57C:/159753852\n"
	 + "BANK NAME\n"
	 + "MELBOURNE, AU\n"
	 + ":59A:/987654321\n"
	 + "BANKFRPPXXX\n"
	 + ":59F:/951357852\n"
	 + "Client Name\n"
	 + "PARIS, FR\n"
	 + ":59:/789456123\n"
	 + "Beneficiary XYZ\n"
	 + "ROME, IT\n"
	 + ":70:/BENEF/Payment for invoice 5678\n"
	 + ":26T:XYZ456\n"
	 + ":77B:/INS/Additional details\n"
	 + ":33B:USD2500,00\n"
	 + ":71A:SHA\n"
	 + ":71F:USD10,00\n"
	 + ":71F:EUR5,00\n"
	 + ":71G:USD2,50\n"
	 + ":36:1,25\n\n\n"
	 + ":21:PREVREF456\n"
	 + ":32B:EUR5000,50\n"
	 + ":50A:/852963741\n"
	 + "BANKHKHKXXX\n"
	 + ":50F:/357159456\n"
	 + "Corporation ABC\n"
	 + "Singapore\n"
	 + ":50K:/654123789\n"
	 + "Private Ltd\n"
	 + "Seoul, KR\n"
	 + ":52A:/159753456\n"
	 + "BANKKRSEXXX\n"
	 + ":52B:/C/951753258\n"
	 + "BANK NAME\n"
	 + "BANGKOK, TH\n"
	 + ":52C:/753951456\n"
	 + "BANK NAME\n"
	 + "NEW DELHI, IN\n"
	 + ":57A:/258963741\n"
	 + "BANKZAJJXXX\n"
	 + ":57C:/654789123\n"
	 + "BANK NAME\n"
	 + "CAPE TOWN, ZA\n"
	 + ":59A:/159357456\n"
	 + "BANKITXXXXX\n"
	 + ":59F:/753951456\n"
	 + "Client XYZ\n"
	 + "ROME, IT\n"
	 + ":59:/456987321\n"
	 + "Beneficiary Ltd\n"
	 + "MADRID, ES\n"
	 + ":70:/BENEF/Payment for order 1234\n"
	 + ":26T:ABC789\n"
	 + ":77B:/INS/Special handling\n"
	 + ":33B:EUR5000,50\n"
	 + ":71A:OUR\n"
	 + ":71F:EUR7,00\n"
	 + ":71F:USD3,50\n"
	 + ":71G:EUR2,75\n"
	 + ":36:1,35\n\n\n"
	 + ":32A:240328USD7500,50\n"
	 + ":19:USD7500,50\n"
	 + ":71G:USD3,75\n"
	 + ":13C:/123456/20250328\n"
	 + ":13C:/789012/20250329\n"
	 + ":53A:/987654321\n"
	 + "BANKSGSGXXX\n"
	 + ":53C:/654789123\n"
	 + "BANK NAME\n"
	 + "DUBAI, AE\n"
	 + ":54A:/852963741\n"
	 + "BANKCNBJXXX\n"
	 + ":72:/INS/Summary instructions\n"
	 + "-}\n"
	 + "{5:{MAC:88B4F929}{CHK:22EF370A4073}}\n";
        
        final Pattern pattern = Pattern.compile(regexBlock4);
        final Matcher matcher = pattern.matcher(string);
        int count=1;
//        while(matcher.find()) {
//        	count++;
//        	System.out.println("TEST "+count);
//        }
        if (matcher.find()) {
            //System.out.println("Full match: " + matcher.group(0));
            System.out.println("######### Group Count: " + matcher.groupCount());
            for (int i = 0; i < matcher.groupCount(); i++) {
                //System.out.println("Group " + i + ": " + matcher.group(i));
//                final Pattern childPattern = Pattern.compile(childRegex);
//                final Matcher childMatcher = childPattern.matcher(matcher.group(i));
//                int count = 1;
//                while (childMatcher.find()) {
//                    //System.out.println("Child Full match: " + childMatcher.group(0));
//                    System.out.println("######### Child Count: " + count++);
//                    for (int j = 1; j < childMatcher.groupCount(); j++) {
//                        System.out.println("###### Child Group " + j + ": " + childMatcher.group(j));
//                    }
//                }
            	String[] lines = matcher.group(i).split("\n");

                List<String> validFields = new ArrayList<>();
                List<String> errors = new ArrayList<>();

                Pattern validFieldPattern = Pattern.compile("^:(\\d{2}[A-Z]?):(.*)");

                for (int k = 0; k < lines.length; k++) {
                    String line = lines[k].trim();

                    Matcher matcher2 = validFieldPattern.matcher(line);
                    if (matcher2.find()) {
                        validFields.add("✔ Valid field: " + line);
                    } else if (line.matches("^(\\d{1,2}[A-Za-z]?:.*|\\d{1,2}:.*)")) {
                        errors.add("❌ Missing colon at Block-4 Field tag >\t" + line);
                    } else if (line.matches("^:.*") || line.matches("^:\\d{1,2}[A-Za-z]?:?.*")) {
                        errors.add("❌ Malformed field tag at Block-4 >\t" + line);
                    } else {
                        //errors.add("⚠ Possibly continuation or orphan text at line " + (k + 1) + ": " + line);
                    }
                }

                // Output results
                System.out.println("=== Valid Fields ===");
                validFields.forEach(System.out::println);

                System.out.println("\n=== Errors === : Proper field tag format (:XX: or :XXA:) like (:20: or :52B:)");
                errors.forEach(System.out::println);
            	
            	
            	
            }
        }
    }
}
