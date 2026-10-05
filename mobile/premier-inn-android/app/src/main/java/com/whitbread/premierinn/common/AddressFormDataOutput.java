package com.whitbread.premierinn.common;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class AddressFormDataOutput {

    public abstract int countryIndex();

    public abstract String text();

    public abstract boolean focus();

    public abstract Form form();

    public enum Form {
        COUNTRY,
        POSTCODE,
        ADDRESS_LINE_1,
        ADDRESS_LINE_2,
        ADDRESS_LINE_3,
        COMPANY,
        COMPANY_SPECIAL_CHARACTER
    }

    public static AddressFormDataOutput create(String text, boolean focus, Form form) {
        return new AutoValue_AddressFormDataOutput(-1, text, focus, form);
    }

    public static AddressFormDataOutput create(int index, Form form) {
        return new AutoValue_AddressFormDataOutput(index, "", false, form);
    }


}
