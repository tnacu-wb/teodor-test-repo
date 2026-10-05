package com.whitbread.premierinn.hoteldetails;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Content implements Parcelable {

    public abstract ContentType contentType();

    public abstract String text();

    public static Builder builder() {
        return new AutoValue_Content.Builder();
    }

    enum ContentType {
        HEADER, BODY, BODY_WITH_HTML, LIST, INLINE, INLINE_WITH_PURPLE_COLOUR, INLINE_BOLD, DIVIDER
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder contentType(ContentType contentType);

        public abstract Builder text(String text);

        public abstract Content build();
    }
}