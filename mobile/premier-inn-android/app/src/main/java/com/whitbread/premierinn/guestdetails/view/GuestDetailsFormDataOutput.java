package com.whitbread.premierinn.guestdetails.view;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class GuestDetailsFormDataOutput {

    public abstract String text();

    public abstract boolean focus();

    public abstract Form form();

    public enum Form {
        TITLE,
        FIRST_NAME,
        LAST_NAME,
        CONTACT_NUMBER,
        EMAIL,
        PASSWORD
    }

    public static GuestDetailsFormDataOutput create(String text, boolean focus, Form form) {
        return new AutoValue_GuestDetailsFormDataOutput(text, focus, form);
    }
}
