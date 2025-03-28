package com.Vcidex.StoryboardSystems.Utils;

public class ExtentLogUtil {

    /**
     * Wraps the provided title and details in an HTML collapsible block using the
     * <details> and <summary> tags.
     *
     * @param title   The summary title to display.
     * @param details The detailed log content.
     * @return A string containing the HTML for a collapsible log block.
     */
    public static String wrapLog(String title, String details) {
        // Replace newlines with HTML <br> for proper formatting in HTML
        String formattedDetails = details.replaceAll("\n", "<br>");
        return "<details><summary><b>" + title + " (Click to expand)</b></summary>"
                + "<pre>" + formattedDetails + "</pre></details>";
    }
}