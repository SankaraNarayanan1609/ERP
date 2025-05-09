package com.Vcidex.StoryboardSystems.Utils.Helpers;

import org.apache.commons.lang3.StringUtils;

public class LocatorUtils {

    private LocatorUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * This method escapes single quotes in the provided text to make it safe for use in XPath.
     * It ensures that any single quotes are correctly handled using concat().
     *
     * @param text The text that needs to be escaped for XPath.
     * @return A string with escaped single quotes for XPath compatibility.
     */
    public static String escapeXPathText(String text) {
        if (StringUtils.isEmpty(text)) {
            return "''"; // Return empty quotes if the text is null or empty
        }

        // If the text contains single quotes, escape them using concat()
        if (text.contains("'")) {
            String[] parts = text.split("'");
            StringBuilder xpath = new StringBuilder("concat(");

            // Use String.join for cleaner string concatenation
            for (int i = 0; i < parts.length; i++) {
                xpath.append("'").append(parts[i]).append("'");
                if (i != parts.length - 1) {
                    xpath.append(", \"'\", ");
                }
            }
            xpath.append(")");
            return xpath.toString();
        }

        // If no single quotes are present, simply wrap the text in single quotes for XPath
        return "'" + text + "'";
    }
}