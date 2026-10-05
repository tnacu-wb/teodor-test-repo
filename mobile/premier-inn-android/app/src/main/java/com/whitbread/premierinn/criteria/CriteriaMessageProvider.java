package com.whitbread.premierinn.criteria;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.whitbread.premierinn.R;

public class CriteriaMessageProvider {

    private final Context context;

    public CriteriaMessageProvider(Context context) {
        this.context = context;
    }

    public String getOneYearReservationMessage(int numberOfNights) {
        return context.getResources()
                .getQuantityString(R.plurals.criteria_tooltip_message, numberOfNights, numberOfNights);
    }

    public String getTooManyNightsMessage(@NonNull String messageFromServer) {
        return createCallUsInfoMessage(messageFromServer, R.string.criteria_max_number_of_nights_message);
    }

    public String getTooManyRoomsMessage(@NonNull String messageFromServer) {
        return createCallUsInfoMessage(messageFromServer, R.string.criteria_max_number_of_rooms_message);
    }

    private String createCallUsInfoMessage(@NonNull String infoMessage, @StringRes int contextSpecificMessageId) {
        String info = "".equals(infoMessage) ? context.getString(R.string.call_view_call_us_prompt) : infoMessage;
        return context.getString(contextSpecificMessageId) + " " + info;
    }

    public String getTooManyAdultMessage() {
        return context.getString(R.string.criteria_max_number_adult);
    }

    public String getTooManyChildrenMessage() {
        return context.getString(R.string.criteria_max_number_children);
    }

    public String getTooManyInfantsMessage() {
        return context.getString(R.string.criteria_screen_max_infants);
    }

    public String getNoCotAvailableMessage() {
        return context.getString(R.string.criteria_no_cot_available);
    }

}
