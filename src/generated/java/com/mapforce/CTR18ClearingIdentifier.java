package com.mapforce;
import java.lang.StringBuilder;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Arrays;

public class CTR18ClearingIdentifier {

    private static final Set<String> isoExtClrSysIdCodes = new HashSet<>(
        Arrays.asList("DEBLZ", "USABA", "CHBCC", "CHSIC") // Add all valid ISO codes
    );

    private static final Set<String> mtClrSysIdCodes = new HashSet<>(
        Arrays.asList("BL", "FW", "US", "CH", "SW")
    );

    public static String transformClearingIdentifier(String mtPartyIdentifier) {
        if (mtPartyIdentifier == null || mtPartyIdentifier.trim().isEmpty()) {
            return null;
        }

        mtPartyIdentifier = mtPartyIdentifier.trim(); // Handle trailing spaces
        String mtClearingSystem;
        String mtClearingCode;
        String mxClearingSystem;
        StringBuilder sb = new StringBuilder();

        // Case 1: //RT// + 5-char system
        if (mtPartyIdentifier.startsWith("/RT//")) {
            String candidate = mtPartyIdentifier.substring(5);
            if (candidate.length() >= 5) {
                mtClearingSystem = candidate.substring(0, 5);
                mtClearingCode = candidate.substring(5);
                if (mtClearingSystem.matches("[A-Z]{5}") &&
                    isoExtClrSysIdCodes.contains(mtClearingSystem) &&
                    !mtClearingCode.isEmpty()) {

                	sb.append(mtClearingSystem);
                	sb.append("/");
                	sb.append(mtClearingCode);
                	return sb.toString();

                }
            }
        }

        // Case 2: // + 5-char system
        if (mtPartyIdentifier.startsWith("/")) {
            String candidate = mtPartyIdentifier.substring(1);
            if (candidate.length() >= 5) {
                mtClearingSystem = candidate.substring(0, 5);
                mtClearingCode = candidate.substring(5);
                if (mtClearingSystem.matches("[A-Z]{5}") &&
                    isoExtClrSysIdCodes.contains(mtClearingSystem) &&
                    !mtClearingCode.isEmpty()) {

                	sb.append(mtClearingSystem);
                	sb.append("/");
                	sb.append(mtClearingCode);
                	return sb.toString();
                }
            }
        }

        // Case 3: 2-char fallback
        try {
            if (mtPartyIdentifier.startsWith("/RT//")) {
                mtClearingSystem = mtPartyIdentifier.substring(5, 7);
                mtClearingCode = mtPartyIdentifier.substring(7);
            } else if (mtPartyIdentifier.startsWith("/")) {
                mtClearingSystem = mtPartyIdentifier.substring(1, 3);
                mtClearingCode = mtPartyIdentifier.substring(3);
            } else {
            	 return "";
            }

            if (!mtClearingSystem.matches("[A-Z]{2}") ||
                !mtClrSysIdCodes.contains(mtClearingSystem) ||
                mtClearingCode.isEmpty()) {
                 return "";
            }

            switch (mtClearingSystem) {
                case "BL":
                    mxClearingSystem = "DEBLZ";
                    break;
                case "FW":
                case "US":
                    mxClearingSystem = "USABA";
                    break;
                case "CH":
                    mxClearingSystem = "CHBCC";
                    break;
                case "SW":
                    mxClearingSystem = (mtClearingCode.length() > 5) ? "CHSIC" : "CHBCC";
                    break;
                default:
                    return "";
            }

            sb.append(mxClearingSystem);
            sb.append("/");
            sb.append(mtClearingCode);
            return sb.toString();


        } catch (Exception e) {
            return "";
        }
    }
  
  // check the MTClearingSystemCodeInList Is True
    public static boolean IsMTClearingSystemCodeInList(String mtPartyIdentifier) {
        if (mtPartyIdentifier == null || mtPartyIdentifier.trim().isEmpty()) {
            return false;
        }

        mtPartyIdentifier = mtPartyIdentifier.trim();
        String mtClearingSystem;
        String mtClearingCode;

        // Case 1: //RT// + 5-char system
        if (mtPartyIdentifier.startsWith("/RT//")) {
            String candidate = mtPartyIdentifier.substring(5);
            if (candidate.length() >= 5) {
                mtClearingSystem = candidate.substring(0, 5);
                mtClearingCode = candidate.substring(5);
                if (mtClearingSystem.matches("[A-Z]{5}") &&
                		mtClrSysIdCodes.contains(mtClearingSystem) &&
                    !mtClearingCode.isEmpty()) {
                    return true;
                }
            }
        }
        // Case 2: // + 5-char system

        if (mtPartyIdentifier.startsWith("/")) {
            String candidate = mtPartyIdentifier.substring(1);
            if (candidate.length() >= 5) {
                mtClearingSystem = candidate.substring(0, 5);
                mtClearingCode = candidate.substring(5);
                if (mtClearingSystem.matches("[A-Z]{5}") &&
                		mtClrSysIdCodes.contains(mtClearingSystem) &&
                    !mtClearingCode.isEmpty()) {
                    return true;
                }
            }
        }

     // Case 3: 2-char fallback
        
        try {
            if (mtPartyIdentifier.startsWith("/RT//")) {
                mtClearingSystem = mtPartyIdentifier.substring(5, 7);
                mtClearingCode = mtPartyIdentifier.substring(7);
            } else if (mtPartyIdentifier.startsWith("/")) {
                mtClearingSystem = mtPartyIdentifier.substring(1, 3);
                mtClearingCode = mtPartyIdentifier.substring(3);
            } else {
                return false;
            }

            return mtClrSysIdCodes.contains(mtClearingSystem) && !mtClearingCode.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

   
   
}

