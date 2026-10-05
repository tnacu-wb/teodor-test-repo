package com.whitbread.premierinn.paymentdetails;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class DateFieldInfo {

    public abstract String date();
    public abstract boolean focus();
    public abstract int cursorPosition();

    public static DateFieldInfo create(@NonNull CharSequence date, boolean focus, int cursorPosition) {
        return new AutoValue_DateFieldInfo(date.toString(), focus, cursorPosition);
    }
}