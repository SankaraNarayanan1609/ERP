package com.Vcidex.StoryboardSystems.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
public class DateUtil {

    /**
     * Parses a date string in "MM/dd/yyyy" format and returns an array: [day, full month name, year]
     *
     * @param excelDate - the date string from Excel, e.g., "04/22/2025"
     * @return String[] - {day, month, year}
     */
    public static String[] parseExcelDate(String excelDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate date = LocalDate.parse(excelDate, formatter);

        String day = String.valueOf(date.getDayOfMonth());
        String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String year = String.valueOf(date.getYear());

        return new String[]{day, month, year};
    }
}
