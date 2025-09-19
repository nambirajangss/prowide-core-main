package com.gss.vmc;


/**
 * Formats MT fields, including truncation, padding, and multi-line splitting (e.g., 4*35x).
 */
public class FieldFormatter {

    /**
     * Formats a string into multiple lines with max length per line.
     *
     * @param input Input string.
     * @param maxLineLength Max characters per line.
     * @param maxLines Max number of lines.
     * @return Formatted multi-line string with CRLF.
     */
    public String formatToMultiLine(String input, int maxLineLength, int maxLines) {
        if (input == null || input.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        int start = 0;
        for (int i = 1; i <= maxLines; i++) {
            int end = Math.min(start + maxLineLength, input.length());
            sb.append(input.substring(start, end));
            if (i < maxLines && end < input.length()) {
                sb.append("\r\n");
            }
            start = end;
            if (start >= input.length()) break;
        }
        // Truncate if exceeds max lines
        return sb.toString();
    }

    // Add more methods: truncation, padding, etc.
}

