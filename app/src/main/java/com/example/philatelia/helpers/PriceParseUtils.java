package com.example.philatelia.helpers;

import com.example.philatelia.data.CartItemEntity;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Shared parsing of price strings for cart items (руб., spaces, comma as decimal separator).
 */
public final class PriceParseUtils {

    private PriceParseUtils() {
    }

    public static double parsePriceToRubles(String raw) {
        if (raw == null || raw.isEmpty()) {
            return 0.0;
        }
        String s = raw.replace('\u00A0', ' ').toLowerCase(Locale.ROOT);
        s = s.replace("бел.р.", "").replace("руб.", "").replace("руб", "").trim();
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c) || c == ',' || c == '.') {
                digits.append(c);
            }
        }
        String num = digits.toString();
        if (num.isEmpty()) {
            return 0.0;
        }
        int lastComma = num.lastIndexOf(',');
        int lastDot = num.lastIndexOf('.');
        int sep = Math.max(lastComma, lastDot);
        if (sep < 0) {
            try {
                return Double.parseDouble(num);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        String intPart = num.substring(0, sep).replaceAll("[.,]", "");
        String frac = num.substring(sep + 1).replaceAll("[^0-9]", "");
        if (frac.length() > 2) {
            frac = frac.substring(0, 2);
        }
        if (intPart.isEmpty()) {
            intPart = "0";
        }
        try {
            return Double.parseDouble(intPart + "." + (frac.isEmpty() ? "0" : frac));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static int rublesToKopecks(double rubles) {
        return (int) Math.round(rubles * 100.0);
    }

    public static String formatDisplayRubles(double rubles) {
        return String.format(Locale.US, "%.2f руб.", rubles);
    }

    /**
     * Sets {@code price} (display), {@code priceNum}, and {@code priceKopecks} from a raw price string.
     */
    public static void applyPriceFields(CartItemEntity item, String rawPrice) {
        double rub = parsePriceToRubles(rawPrice);
        item.priceNum = rub;
        item.priceKopecks = rublesToKopecks(rub);
        item.price = formatDisplayRubles(rub);
    }

    public static String stableStampId(String title, String price, String imageUrl) {
        return "s_" + Objects.hash(title, price, imageUrl);
    }

    public static String stableSetId(String name, String price, String year) {
        return "set_" + Objects.hash(name, price, year);
    }

    public static String computeTotalFormatted(List<CartItemEntity> items) {
        if (items == null || items.isEmpty()) {
            return "0.00";
        }
        int sumKopecks = 0;
        for (CartItemEntity item : items) {
            int k = item.priceKopecks;
            if (k <= 0 && item.priceNum > 0) {
                k = rublesToKopecks(item.priceNum);
            }
            if (k <= 0 && item.price != null) {
                k = rublesToKopecks(parsePriceToRubles(item.price));
            }
            int qty = item.quantity > 0 ? item.quantity : 1;
            sumKopecks += k * qty;
        }
        return String.format(Locale.US, "%.2f", sumKopecks / 100.0);
    }
}
