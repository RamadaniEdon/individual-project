package com.database.federation.utils;

public class NumberParser {
    

    public static Double parseStringToDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
