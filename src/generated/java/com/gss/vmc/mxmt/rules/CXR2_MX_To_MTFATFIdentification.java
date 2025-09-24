package com.gss.vmc.mxmt.rules;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Java class to translate Swift MX (XML) to MT (JSON) for FATF ID handling based on provided pseudocode.
 * 
	List<Map<String, String>>
		"schemeNameCode"
		"issuer"
		"identification"
	List<Map<String, String>>
 		"schemeNameCode"
		"issuer"
		"identification"
	String
	List<String>
	String
 
 * Input HashMap Structure:
 * - key: "organisationOtherList"
 *   value-Data-Type: List<Map<String, String>>
 *   Pseudocode-XMLPath: Identification.OrganisationIdentification.Other
 * 	 PACS08/PACS09-XMLPath: ?????
 *   Description: List of organization identification details, each map containing schemeNameCode, issuer, and identification.
 * - key: "privateOtherList"
 *   value-Data-Type: List<Map<String, String>>
 *   Pseudocode-XMLPath: Identification.PrivateIdentification.Other
 *   Description: List of private identification details, each map containing schemeNameCode, issuer, and identification.
 * - key: "schemeNameCode"
 *   value-Data-Type: String
 *   Pseudocode-XMLPath: Identification.OrganisationIdentification.Other[i].SchemeName.Code or Identification.PrivateIdentification.Other[i].SchemeName.Code
 *   Description: Code identifying the scheme (e.g., GS1G, DUNS, TXID, ARNU).
 * - key: "issuer"
 *   value-Data-Type: String
 *   Pseudocode-XMLPath: Identification.OrganisationIdentification.Other[i].Issuer or Identification.PrivateIdentification.Other[i].Issuer
 *   Description: Issuer of the identification, may include country code.
 * - key: "identification"
 *   value-Data-Type: String
 *   Pseudocode-XMLPath: Identification.OrganisationIdentification.Other[i].Identification or Identification.PrivateIdentification.Other[i].Identifier
 *   Description: The actual identifier value.
 * - key: "postalAddressCountry"
 *   value-Data-Type: String
 *   Pseudocode-XMLPath: MXPartyIdentification.PostalAddress.Country
 *   Description: Country code from the postal address.
 * - key: "addressLineList"
 *   value-Data-Type: List<String>
 *   Pseudocode-XMLPath: MXPartyIdentification.PostalAddress.AddressLine
 *   Description: List of address lines, some starting with "2/" or "3/" for structured addresses.
 * - key: "countryOfResidence"
 *   value-Data-Type: String
 *   Pseudocode-XMLPath: MXPartyIdentification.CountryOfResidence
 *   Description: Country of residence for private identification fallback.
 * 
 * Output HashMap Structure:
 * - key: "mtFatfId"
 *   value-Data-Type: String
 *   Pseudocode-Output: MTFATFId
 *   Description: Final concatenated FATF ID string (e.g., SchemeCode/CountryCode/Issuer/Identifier).
 * - key: "mtCountryCode"
 *   value-Data-Type: String
 *   Pseudocode-Output: MTCountryCode
 *   Description: Extracted country code (e.g., from issuer or address).
 * - key: "mtSchemeCode"
 *   value-Data-Type: String
 *   Pseudocode-Output: MTSchemeCode
 *   Description: Scheme code used (e.g., CUST, TXID).
 * - key: "mtIssuer"
 *   value-Data-Type: String
 *   Pseudocode-Output: MTIssuer
 *   Description: Issuer value, may be empty or "NOTPROVIDED".
 * - key: "mtIdentifier"
 *   value-Data-Type: String
 *   Pseudocode-Output: MTIdentifier
 *   Description: Identifier value from input.
 * - key: "mtPartyIdentifier"
 *   value-Data-Type: String
 *   Pseudocode-Output: MTPartyIdentifier
 *   Description: Final identifier, truncated if >35 characters, or "/NOTPROVIDED" if unsuccessful.
 * - key: "mtCode8"
 *   value-Data-Type: String
 *   Pseudocode-Output: MTCode8
 *   Description: Overflow of mtFatfId if >35 characters, prefixed with "8/".
 * - key: "successfulFatf"
 *   value-Data-Type: Boolean
 *   Pseudocode-Output: SuccessfulFATF
 *   Description: Indicates if FATF ID translation was successful.
 * - key: "errorCodes"
 *   value-Data-Type: List<String>
 *   Pseudocode-Output: T200058, T12001, etc.
 *   Description: List of error/warning codes triggered during processing.
 */
public class CXR2_MX_To_MTFATFIdentification {
    // Constants for scheme codes
    private static final Set<String> PRIORITY_SCHEMES = Set.of("GS1G", "DUNS", "TXID");
    private static final Set<String> PRIVATE_SCHEMES = Set.of("ARNU", "CCPT", "NIDN", "SOSE", "TXID", "CUST", "DRLC", "EMPL");
    private static final Set<String> ISO_LIST = Set.of("GS1G", "DUNS", "TXID", "CUST", "EMPL"); // Placeholder for ISO LIST
    private static final Set<String> EXCLUDED_SCHEMES = Set.of("CUST", "EMPL", "TXID");

    // Helper method stubs
    boolean isPresent(Object obj) {
        return obj != null && (!(obj instanceof String) || !((String) obj).isEmpty());
    }

    boolean isCountryCode(String code) {
        return code != null && code.matches("[A-Z]{2}");
    }

    boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    String substring(String str, int start, int end) {
        if (str == null || start > str.length()) return "";
        if (end > str.length()) end = str.length();
        return str.substring(start - 1, end);
    }

    String concatenate(String... parts) {
        return String.join("", parts);
    }
    
    /**
     * Translates MX Party Identification to MT FATF ID format.
     *
     * @param input HashMap containing XML elements as per pseudocode
     * @return HashMap containing output variables
     */
    public Map<String, Object> translate(Map<String, Object> input) {
        Map<String, Object> output = new HashMap<>();
        // Initialize local variables
        String mtFatfId = "";
        String mtCountryCode = "";
        String mtSchemeCode = "";
        String mtIssuer = "";
        String mtIdentifier = "";
        String mtPartyIdentifier = "";
        String mtCode8 = "";
        boolean successfulFatf = false;
        List<String> errorCodes = new ArrayList<>();

        
        
        // Case 1 & 2: Organisation Identification
        List<Map<String, String>> orgOtherList = (List<Map<String, String>>) input.getOrDefault("organisationOtherList", Collections.emptyList());
        if (!orgOtherList.isEmpty()) {
            // Case 1: Search for GS1G, DUNS, TXID
            for (Map<String, String> other : orgOtherList) {
                String mxCode = other.getOrDefault("schemeNameCode", "");
                String mxIssuer = other.getOrDefault("issuer", "");
                mtIdentifier = other.getOrDefault("identification", "");

                // Extract Country Code
                if (!isEmpty(mxIssuer) && isCountryCode(substring(mxIssuer, 1, 2))) {
                    if (mxIssuer.length() == 2) {
                        mtCountryCode = mxIssuer;
                        mxIssuer = "";
                    } else if (mxIssuer.length() > 3 && substring(mxIssuer, 3, 3).equals("/")) {
                        mtCountryCode = substring(mxIssuer, 1, 2);
                        mxIssuer = substring(mxIssuer, 4, mxIssuer.length());
                    }
                }

                // GS1G / DUNS
                if (PRIORITY_SCHEMES.contains(mxCode) && (mxCode.equals("GS1G") || mxCode.equals("DUNS"))) {
                    mtSchemeCode = "CUST";
                    mtIssuer = isEmpty(mxIssuer) ? mxCode : concatenate(mxCode, " ", mxIssuer);
                    if (!isEmpty(mtCountryCode)) {
                        successfulFatf = true;
                        break;
                    }
                }
                // TXID
                else if (mxCode.equals("TXID")) {
                    mtSchemeCode = mxCode;
                    mtIssuer = "";
                    if (!isEmpty(mtCountryCode) && !mtIdentifier.equals("NOTPROVIDED")) {
                        successfulFatf = true;
                        break;
                    }
                }
            }

            // Case 2: If Case 1 failed
            if (!successfulFatf) {
                for (Map<String, String> other : orgOtherList) {
                    String mxCode = other.getOrDefault("schemeNameCode", "");
                    String mxIssuer = other.getOrDefault("issuer", "");
                    mtIdentifier = other.getOrDefault("identification", "");

                    // Extract Country Code
                    if (!isEmpty(mxIssuer) && isCountryCode(substring(mxIssuer, 1, 2))) {
                        if (mxIssuer.length() == 2) {
                            mtCountryCode = mxIssuer;
                            mxIssuer = "";
                        } else if (mxIssuer.length() > 3 && substring(mxIssuer, 3, 3).equals("/")) {
                            mtCountryCode = substring(mxIssuer, 1, 2);
                            mxIssuer = substring(mxIssuer, 4, mxIssuer.length());
                        }
                    }

                    // ISO List Codes (not CUST, EMPL, TXID)
                    if (ISO_LIST.contains(mxCode) && !EXCLUDED_SCHEMES.contains(mxCode)) {
                        mtSchemeCode = "CUST";
                        mtIssuer = isEmpty(mxIssuer) ? mxCode : concatenate(mxCode, " ", mxIssuer);
                        if (!isEmpty(mtCountryCode)) {
                            successfulFatf = true;
                            break;
                        }
                    }
                    // CUST / EMPL
                    else if (mxCode.equals("CUST") || mxCode.equals("EMPL")) {
                        mtSchemeCode = mxCode;
                        mtIssuer = isEmpty(mxIssuer) ? "NOTPROVIDED" : mxIssuer;
                        if (!isEmpty(mtCountryCode)) {
                            successfulFatf = true;
                            break;
                        }
                    }
                    // TXID
                    else if (mxCode.equals("TXID")) {
                        mtSchemeCode = mxCode;
                        mtIssuer = "";
                        if (!isEmpty(mtCountryCode) && !mtIdentifier.equals("NOTPROVIDED")) {
                            successfulFatf = true;
                            break;
                        }
                    }
                }

                // Case 3: No successful match
                if (!successfulFatf) {
                    String mxCode = orgOtherList.isEmpty() ? "" : orgOtherList.get(0).getOrDefault("schemeNameCode", "");
                    if (mxCode.equals("TXID") && mtIdentifier.equals("NOTPROVIDED")) {
                        // No translation
                    } else if (ISO_LIST.contains(mxCode)) {
                        String postalCountry = (String) input.getOrDefault("postalAddressCountry", "");
                        if (isPresent(postalCountry)) {
                            mtCountryCode = postalCountry;
                            successfulFatf = true;
                            errorCodes.addAll(Arrays.asList("T200058", "T12001"));
                        }

                        List<String> addressLines = (List<String>) input.getOrDefault("addressLineList", Collections.emptyList());
                        if (!addressLines.isEmpty()) {
                            boolean structuredAddressIndicator = mxToMtAddressLineType(input);
                            if (structuredAddressIndicator) {
                                for (String line : addressLines) {
                                    if (substring(line, 1, 2).equals("3/")) {
                                        mtCountryCode = substring(line, 3, 4);
                                        successfulFatf = true;
                                        errorCodes.add("T12001");
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        errorCodes.addAll(Arrays.asList("T20217", "T12010"));
                    }
                }
            }
        }
        // Case 2: Private Identification
        else {
            List<Map<String, String>> privateOtherList = (List<Map<String, String>>) input.getOrDefault("privateOtherList", Collections.emptyList());
            if (!privateOtherList.isEmpty()) {
                for (Map<String, String> other : privateOtherList) {
                    mtSchemeCode = other.getOrDefault("schemeNameCode", "");
                    mtIssuer = other.getOrDefault("issuer", "");
                    mtIdentifier = other.getOrDefault("identification", "");

                    if (PRIVATE_SCHEMES.contains(mtSchemeCode) || mtSchemeCode.equals("ISO ExternalPersonIdentification1Code")) {
                        if (!isEmpty(mtIssuer) && isCountryCode(substring(mtIssuer, 1, 2))) {
                            if (mtIssuer.length() == 2) {
                                mtCountryCode = mtIssuer;
                                mtIssuer = "";
                                successfulFatf = true;
                                break;
                            } else if (mtIssuer.length() > 3 && substring(mtIssuer, 3, 3).equals("/")) {
                                mtCountryCode = substring(mtIssuer, 1, 2);
                                mtIssuer = substring(mtIssuer, 4, mtIssuer.length());
                                successfulFatf = true;
                                break;
                            }
                        }
                    }
                }

                // Defaults if unsuccessful
                if (!successfulFatf && (PRIVATE_SCHEMES.contains(mtSchemeCode) || mtSchemeCode.equals("ISO ExternalPersonIdentification1Code"))) {
                    String postalCountry = (String) input.getOrDefault("postalAddressCountry", "");
                    if (isPresent(postalCountry)) {
                        mtCountryCode = postalCountry;
                        successfulFatf = true;
                        errorCodes.addAll(Arrays.asList("T200058", "T12001"));
                    } else {
                        List<String> addressLines = (List<String>) input.getOrDefault("addressLineList", Collections.emptyList());
                        if (!addressLines.isEmpty()) {
                            boolean structuredAddressIndicator = mxToMtAddressLineType(input);
                            if (structuredAddressIndicator) {
                                for (String line : addressLines) {
                                    if (substring(line, 1, 2).equals("3/")) {
                                        mtCountryCode = substring(line, 3, 4);
                                        successfulFatf = true;
                                        errorCodes.add("T12001");
                                        break;
                                    }
                                }
                            }
                        } else {
                            String countryOfResidence = (String) input.getOrDefault("countryOfResidence", "");
                            if (isPresent(countryOfResidence)) {
                                mtCountryCode = countryOfResidence;
                                successfulFatf = true;
                                errorCodes.addAll(Arrays.asList("T200059", "T12002"));
                            }
                        }
                    }
                }

                if (!PRIVATE_SCHEMES.contains(mtSchemeCode) && !mtSchemeCode.equals("ISO ExternalPersonIdentification1Code")) {
                    successfulFatf = false;
                    errorCodes.addAll(Arrays.asList("T200060", "T12003"));
                }

                if (Set.of("ARNU", "CCPT", "NIDN", "SOSE", "TXID").contains(mtSchemeCode)) {
                    mtIssuer = "";
                }

                if (mtSchemeCode.equals("ISO ExternalPersonIdentification1Code") && !PRIVATE_SCHEMES.contains(mtSchemeCode)) {
                    mtIssuer = isEmpty(mtIssuer) ? mtSchemeCode : concatenate(mtSchemeCode, " ", mtIssuer);
                    mtSchemeCode = "CUST";
                }

                if (Set.of("CUST", "DRLC", "EMPL").contains(mtSchemeCode) && isEmpty(mtIssuer)) {
                    mtIssuer = "NOTPROVIDED";
                }
            }
        }

        // Build Final FATF ID
        if (!successfulFatf) {
            mtPartyIdentifier = "/NOTPROVIDED";
            errorCodes.addAll(Arrays.asList("T200026", "T12004"));
        } else {
            if (isEmpty(mtIssuer)) {
                mtFatfId = concatenate(mtSchemeCode, "/", mtCountryCode, "/", mtIdentifier);
            } else if (mtIssuer.equals("NOTPROVIDED")) {
                mtIssuer = "";
                mtFatfId = concatenate(mtSchemeCode, "/", mtCountryCode, "/", mtIssuer, "/", mtIdentifier);
            } else {
                mtFatfId = concatenate(mtSchemeCode, "/", mtCountryCode, "/", mtIssuer, "/", mtIdentifier);
            }

            // Length Handling
            if (Set.of("EMPL", "CUST", "DRLC").contains(mtSchemeCode) && mtFatfId.length() > 68) {
                int issuerLength = 59 - mtIdentifier.length();
                mtIssuer = concatenate(substring(mtIssuer, 1, issuerLength - 1), "+");
                mtFatfId = concatenate(mtSchemeCode, "/", mtCountryCode, "/", mtIssuer, "/", mtIdentifier);
            }

            // Truncation to MT Identifier + MTCode8
            if (mtFatfId.length() > 35) {
                mtPartyIdentifier = substring(mtFatfId, 1, 35);
                mtCode8 = concatenate("8/", substring(mtFatfId, 36, mtFatfId.length()));
            } else {
                mtPartyIdentifier = mtFatfId;
            }
        }

        // Populate output
        output.put("mtFatfId", mtFatfId);
        output.put("mtCountryCode", mtCountryCode);
        output.put("mtSchemeCode", mtSchemeCode);
        output.put("mtIssuer", mtIssuer);
        output.put("mtIdentifier", mtIdentifier);
        output.put("mtPartyIdentifier", mtPartyIdentifier);
        output.put("mtCode8", mtCode8);
        output.put("successfulFatf", successfulFatf);
        output.put("errorCodes", errorCodes);

        return output;
    }

    /**
     * Implements MX_To_MTAddressLineType logic to determine if address lines are structured.
     *
     * @param input HashMap containing MXPartyIdentification data
     * @return true if address lines are structured, false otherwise
     */
    private boolean mxToMtAddressLineType(Map<String, Object> input) {
        List<String> addressLines = (List<String>) input.getOrDefault("addressLineList", Collections.emptyList());
        if (addressLines.isEmpty()) {
            return false;
        }

        boolean structuredIndicator = true;

        // Check each line starts with "2/" or "3/"
        for (String line : addressLines) {
            if (!Set.of("2/", "3/").contains(substring(line, 1, 2))) {
                return false;
            }
        }

        // Check "2/" cannot follow "3/"
        for (int i = 0; i < addressLines.size(); i++) {
            if (substring(addressLines.get(i), 1, 2).equals("3/")) {
                for (int j = i + 1; j < addressLines.size(); j++) {
                    if (!substring(addressLines.get(j), 1, 2).equals("3/")) {
                        return false;
                    }
                }
            }
        }

        // Check one line starting with "3/" is mandatory and followed by valid country code
        boolean found3Slash = false;
        for (String line : addressLines) {
            if (substring(line, 1, 2).equals("3/")) {
                String countryCode = substring(line, 3, 4);
                String rest = substring(line, 5, line.length());
                if (isCountryCode(countryCode) && (rest.isEmpty() || rest.startsWith("/"))) {
                    found3Slash = true;
                    break;
                } else {
                    return false;
                }
            }
        }
        if (!found3Slash) {
            return false;
        }

        // Check number of lines starting with "2/"
        long count2Slash = addressLines.stream().filter(line -> substring(line, 1, 2).equals("2/")).count();
        if (count2Slash > 2) {
            return false;
        }

        // Check number of lines starting with "3/"
        long count3Slash = addressLines.stream().filter(line -> substring(line, 1, 2).equals("3/")).count();
        if (count3Slash > 2) {
            return false;
        }

        return structuredIndicator;
    }
}
