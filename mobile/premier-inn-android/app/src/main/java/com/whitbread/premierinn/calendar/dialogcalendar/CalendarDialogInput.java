package com.whitbread.premierinn.calendar.dialogcalendar;

import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.threeten.bp.LocalDate;

@AutoValue
public abstract class CalendarDialogInput implements Parcelable {

    public abstract LocalDate rangeStartDate();

    public abstract LocalDate selectedDate();

    @NonNull
    public static CalendarDialogInput create(@NonNull LocalDate rangeStartDate, @NonNull LocalDate selectedDate) {
        return new AutoValue_CalendarDialogInput(rangeStartDate, selectedDate);
    }
}
