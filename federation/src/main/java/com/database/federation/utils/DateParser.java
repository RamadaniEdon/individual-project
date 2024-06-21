package com.database.federation.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.ZoneId;

public class DateParser {

    public static void main(String[] args) {
        String dateStr1 = "Sat Jun 01 03:00:00 EEST 2024";
        String dateStr2 = "2024-06-01";

        Instant date1 = parseDate(dateStr1);
        Instant date2 = parseDate(dateStr2);

        if (date1 != null && date2 != null) {
            System.out.println("Date 1 is before Date 2: " + date2.isBefore(date1));
        } else {
            System.out.println("One of the dates could not be parsed.");
        }
    }

    public static Instant parseDate(String dateStr) {
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ISO_LOCAL_DATE;

        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateStr, formatter1);
            return zonedDateTime.toInstant();
        } catch (DateTimeParseException e) {
            try {
                LocalDate localDate = LocalDate.parse(dateStr, formatter2);
                LocalDateTime localDateTime = localDate.atStartOfDay();
                return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            } catch (DateTimeParseException ex) {
                System.err.println("Failed to parse date: " + dateStr);
                return null;
            }
        }
    }
}
