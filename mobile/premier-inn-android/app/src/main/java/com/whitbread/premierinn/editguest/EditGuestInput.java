package com.whitbread.premierinn.editguest;


import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput;

import java.util.List;

@AutoValue
public abstract class EditGuestInput implements Parcelable {

    public abstract List<GuestDetailsFormDataInput> guestDetailsFormDataInputs();
    public abstract boolean isBookerStaying();
    public abstract GuestDetailsFormDataInput bookerDetails();

    public static EditGuestInput create(@NonNull List<GuestDetailsFormDataInput> guestDetailsFormDataInputs,
                                        @NonNull GuestDetailsFormDataInput bookerDetails, boolean bookerStaying) {
        return new AutoValue_EditGuestInput(guestDetailsFormDataInputs, bookerStaying, bookerDetails);
    }
}
