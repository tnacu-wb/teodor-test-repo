package com.whitbread.premierinn.guestdetails.view;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class GuestDetailsFormDataInput implements Parcelable {

    public static GuestDetailsFormDataInput create(String title, String firstName,
                                                   String lastName, String email, @Nullable String phoneNumber,
                                                   String language) {
        return create(title, firstName, false, lastName, false, email, false, phoneNumber, language);
    }

    public static GuestDetailsFormDataInput create(String title, String firstName,
                                                   String lastName, String email,
                                                   String language) {
        return create(title, firstName, false, lastName, false, email, false, null, language);
    }

    public static GuestDetailsFormDataInput create(String title, String firstName, boolean errorFirstName,
                                                   String lastName, boolean errorLastName, String email,
                                                   boolean errorEmail, String language) {
        return new AutoValue_GuestDetailsFormDataInput(title, language, firstName, errorFirstName, lastName, errorLastName, email,
                errorEmail, null);
    }

    public static GuestDetailsFormDataInput create(String title, String firstName, boolean errorFirstName,
                                                   String lastName, boolean errorLastName, String email,
                                                   boolean errorEmail, @Nullable String phoneNumber,
                                                   String language) {
        return new AutoValue_GuestDetailsFormDataInput(title, language, firstName, errorFirstName, lastName, errorLastName, email,
                errorEmail, phoneNumber);
    }

    public static GuestDetailsFormDataInput createDefault() {
        return create("", "", "", "", "");
    }

    public abstract String title();

    public abstract String language();

    public abstract String firstName();

    public abstract boolean errorFirstName();

    public abstract String lastName();

    public abstract boolean errorLastName();

    @Nullable
    public abstract String email();

    public abstract boolean errorEmail();

    @Nullable
    public abstract String phoneNumber();

    public static GuestDetailsFormDataInput.Builder builder() {
        return new AutoValue_GuestDetailsFormDataInput.Builder();
    }

    public abstract GuestDetailsFormDataInput.Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract GuestDetailsFormDataInput.Builder title(String title);

        public abstract GuestDetailsFormDataInput.Builder language(String language);

        public abstract GuestDetailsFormDataInput.Builder firstName(String firstName);

        public abstract GuestDetailsFormDataInput.Builder errorFirstName(boolean error);

        public abstract GuestDetailsFormDataInput.Builder lastName(String lastName);

        public abstract GuestDetailsFormDataInput.Builder errorLastName(boolean error);

        public abstract GuestDetailsFormDataInput.Builder email(String email);

        public abstract GuestDetailsFormDataInput.Builder errorEmail(boolean error);

        public abstract GuestDetailsFormDataInput.Builder phoneNumber(String phoneNumber);

        public abstract GuestDetailsFormDataInput build();
    }
}
