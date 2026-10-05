package com.whitbread.premierinn.common.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;


public final class StringUtils {
    public static final String EMPTY_STRING = "";
    public static final String SPACE = " ";
    private static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String LINE_BREAK = "\n";

    private StringUtils() {
        throw new AssertionError("no instances allowed");
    }

    @NonNull
    public static <T> String toCommaSeparatedString(@NonNull List<T> list) {
        return stringDelimitedWith(COMMA, list);
    }

    @NonNull
    public static <T> String toColonSeparatedString(@NonNull List<T> list) {
        return stringDelimitedWith(COLON, list);
    }

    private static <T> String stringDelimitedWith(@NonNull String delimiter, @NonNull List<T> list) {
        StringBuilder builder = new StringBuilder();
        if (!list.isEmpty()) {
            builder.append(list.get(0));
            for (int i = 1; i < list.size(); i++) {
                builder.append(delimiter).append(list.get(i));
            }
        }
        return builder.toString();
    }

    public static String valueOrDefault(@Nullable String value, @NonNull String defaultString) {
        return isBlank(value) ? defaultString : value;
    }

    public static String formatWith2DecimalPlaces(float value) {
        return String.format(Locale.UK, "%.2f", value);
    }

    public static boolean isBlank(@Nullable CharSequence string) {
        return (string == null || string.toString().trim().length() == 0);
    }
}