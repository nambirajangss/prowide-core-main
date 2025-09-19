package com.mapforce;
import java.util.Arrays;
public class Codecounter {
    public static String countValidInstructionCodesAsString(String inputCsv) {
        String[] validCodes = {"HOLD","PHOB","CHQB","TELB"};
        int count = 0;

        if (inputCsv == null || inputCsv.trim().isEmpty()) {
            return "0";
        }

        String[] inputCodes = inputCsv.split(",");
        for (String code : inputCodes) {
            for (String valid : validCodes) {
                if (valid.equalsIgnoreCase(code.trim())) {
                    count++;
                    break;
                }
            }
        }

        return String.valueOf(count);
    }
	
public static boolean isValidInstructionList(String inputCsv) {
        String[] validCodes = {"HOLD", "PHOB", "CHQB", "TELB"};
        String[] inputCodes = inputCsv.split(",");

        for (String code : inputCodes) {
            if (Arrays.asList(validCodes).contains(code.trim())) {
                return true; // Found at least one valid code
            }
        }
        return false; // None matched
    }


}
