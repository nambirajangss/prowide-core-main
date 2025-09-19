package com.mapforce;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CTR4Date {

    /**
     * Transforms an MT date string ([YY]YYMMDD) to MX ISODate format (YYYY-MM-DD)
     *
     * @param mtDate the MT format date
     * @return formatted ISO date string or error message
     */
    public static String transformMTToMXDate(String mtDate) {
        try {
            if (mtDate == null || mtDate.trim().isEmpty()) {
                return "ERROR: Input is null or empty";
            }

            mtDate = mtDate.trim();

            String pattern;
            String dateString;

            if (mtDate.length() == 8) {
                pattern = "yyyyMMdd";
                dateString = mtDate;
            } else if (mtDate.length() == 6) {
                pattern = "yyMMdd";
                dateString = mtDate;
            } else {
                return "ERROR: Input length must be 6 or 8 characters";
            }

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(pattern);
            LocalDate parsedDate = LocalDate.parse(dateString, inputFormatter);

            return parsedDate.toString(); // ISO format: yyyy-MM-dd
        } catch (DateTimeParseException e) {
            return "ERROR: Invalid date format";
        } catch (Exception e) {
            return "ERROR: Unexpected error";
        }
    }
}
