package com.whitbread.premierinn.api.response.booking;

import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@Deprecated // Use com.whitbread.premierinn.data.remote.BookingNote
@AutoValue
public abstract class BookingNote implements Parcelable {

    public abstract int priority();

    public abstract String text();

    @NonNull
    public static TypeAdapter<BookingNote> typeAdapter(Gson gson) {
        return new AutoValue_BookingNote.GsonTypeAdapter(gson);
    }
}