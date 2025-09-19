  // You can modify this to match your actual project structure
package com.mapforce;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.IllegalArgumentException;

public class CTR1RateCurrencyAmount {

    // Converts MT rate format to MX-compliant rate format
    public static String MT_To_MXRate(String mtRate) {

        if (mtRate == null || mtRate.isEmpty()) {
            return "";
        }

        // Replace ',' with '.'
        String rate = mtRate.replace(",", ".");

        // Split into integer and fractional parts
        String[] parts = rate.split("\\.");

        String integerPart = parts[0].replaceFirst("^0+(?!$)", ""); // Trim leading zeros
        String fractionalPart = parts.length > 1 ? parts[1].replaceFirst("0+$", "") : "";

        // Build the final MX rate
        StringBuilder mxRate = new StringBuilder();

        if (integerPart.isEmpty()) {
            mxRate.append("0");
        } else {
            mxRate.append(integerPart);
        }

        if (!fractionalPart.isEmpty()) {
            mxRate.append(".").append(fractionalPart);
        }
		
	

        return mxRate.toString();
    }
}
