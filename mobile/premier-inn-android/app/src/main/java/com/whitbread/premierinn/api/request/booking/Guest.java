package com.whitbread.premierinn.api.request.booking;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput;

import java.util.ArrayList;
import java.util.List;

@AutoValue
public abstract class Guest {

    @SerializedName("firstName")
    public abstract String firstName();
    @SerializedName("lastName")
    public abstract String lastName();
    @SerializedName("roomNumber")
    public abstract int roomNumber();
    @SerializedName("title")
    public abstract String title();

    @NonNull
    public static TypeAdapter<Guest> typeAdapter(Gson gson) {
        return new AutoValue_Guest.GsonTypeAdapter(gson);
    }

    public static List<Guest> createGuests(@NonNull List<GuestDetailsFormDataInput> guestDetailsFormDataInputs) {
        List<Guest> guests = new ArrayList<>();
        for (int roomIndex = 0; roomIndex < guestDetailsFormDataInputs.size(); roomIndex++) {
            GuestDetailsFormDataInput input = guestDetailsFormDataInputs.get(roomIndex);
            guests.add(new AutoValue_Guest(input.firstName(), input.lastName(), roomIndex + 1,
                    input.title()));
        }
        return guests;
    }
}
