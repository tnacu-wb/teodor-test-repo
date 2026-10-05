package com.whitbread.premierinn.guestdetails.adapter;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput;

@AutoValue
public abstract class RoomGuestDetailsData {

    public abstract GuestDetailsFormDataOutput formViewData();

    public abstract int position();

    public static RoomGuestDetailsData create(GuestDetailsFormDataOutput guestDetailsFormDataOutput, int position) {
        return new AutoValue_RoomGuestDetailsData(guestDetailsFormDataOutput, position);
    }
}
