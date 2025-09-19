package com.gss.vmc;

public class CXR17ClearingIdentifier {

    public static String transformClearingIdentifier(String mxClearingSystem) {
        if (mxClearingSystem == null || mxClearingSystem.trim().isEmpty()) {
            return "";
        }

        mxClearingSystem = mxClearingSystem.trim().toUpperCase();

        // Validate that input matches one of known 5- or 6-character codes
        if (!mxClearingSystem.matches("[A-Z]{5,6}")) {
            return "";
        }

        String mtClearingCode;
        switch (mxClearingSystem) {
            case "ATBLZ":  mtClearingCode = "AT"; break;
            case "AUBSB":  mtClearingCode = "AU"; break;
            case "DEBLZ":  mtClearingCode = "BL"; break;
            case "CACPA":  mtClearingCode = "CC"; break;
            case "CNAPS":  mtClearingCode = "CN"; break;
            case "USPID":  mtClearingCode = "CP"; break;
            case "ESNCC":  mtClearingCode = "ES"; break;
            case "USABA":  mtClearingCode = "FW"; break;
            case "GRBIC":  mtClearingCode = "GR"; break;
            case "HKNCC":  mtClearingCode = "HK"; break;
            case "IENCC":  mtClearingCode = "IE"; break;
            case "INFSC":  mtClearingCode = "IN"; break;
            case "ITNCC":  mtClearingCode = "IT"; break;
            case "PLKNR":  mtClearingCode = "PL"; break;
            case "PTNCC":  mtClearingCode = "PT"; break;
            case "RUCBC":  mtClearingCode = "RU"; break;
            case "GBDSC":  mtClearingCode = "SC"; break;
            case "CHBCC":  mtClearingCode = "SW"; break;  // SW used for Swiss Clearing
            case "CHSIC":  mtClearingCode = "SW"; break;
            case "ZANCC":  mtClearingCode = "ZA"; break;
            case "NZNCC":  mtClearingCode = "NZ"; break;
            case "SESBA":  mtClearingCode = "SE"; break;
            case "SGIBG":  mtClearingCode = "SG"; break;
            default:      mtClearingCode = null;
        }

        return mtClearingCode != null ? mtClearingCode : "";
    }
}
