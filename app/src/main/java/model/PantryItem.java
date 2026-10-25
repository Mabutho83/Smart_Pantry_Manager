package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** One ingredient the user currently has at home (a row in the "pantry" table). */
public class PantryItem {
    private final long id;
    private final String name;
    private final double quantity;
    private final String unit;
    private final String expiry; // "yyyy-MM-dd" or null/empty when not set

    public PantryItem(long id, String name, double quantity, String unit, String expiry) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getExpiry() { return expiry; }

    /** True if the item has an expiry date that is within the given number of days (or already past). */
    public boolean isExpiringSoon(int days) {
        if (expiry == null || expiry.isEmpty()) return false;
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expiry);
            long diff = d.getTime() - System.currentTimeMillis();
            return diff <= days * 24L * 60 * 60 * 1000;
        } catch (ParseException e) {
            return false;
        }
    }
}
