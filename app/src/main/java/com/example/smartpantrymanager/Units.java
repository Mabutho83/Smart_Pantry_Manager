package com.example.smartpantrymanager;

/** Helpers for comparing quantities that were entered in different units. */
public class Units {
    public static final String[] ALL = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "pcs"};

    public static final int MASS = 0;
    public static final int VOLUME = 1;
    public static final int COUNT = 2;

    /** Which family a unit belongs to. Units can only be converted within the same family. */
    public static int category(String unit) {
        switch (unit.toLowerCase()) {
            case "g":
            case "kg":
                return MASS;
            case "ml":
            case "l":
            case "tsp":
            case "tbsp":
            case "cup":
                return VOLUME;
            default:
                return COUNT;
        }
    }

    /** Converts to the base unit of the family: grams, millilitres, or pieces. */
    public static double toBase(double qty, String unit) {
        switch (unit.toLowerCase()) {
            case "kg":
            case "l":
                return qty * 1000;
            case "tsp":
                return qty * 5;
            case "tbsp":
                return qty * 15;
            case "cup":
                return qty * 240;
            default:
                return qty;
        }
    }

    /** 2.0 -> "2", 2.5 -> "2.5" */
    public static String format(double q) {
        if (q == Math.rint(q)) return String.valueOf((long) q);
        return String.valueOf(q);
    }
}
