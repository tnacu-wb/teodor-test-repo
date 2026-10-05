package com.whitbread.premierinn.reviewbooking.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class LeadGuestDetailsView extends LinearLayout {

    public LeadGuestDetailsView(Context context) {
        super(context);
    }

    public LeadGuestDetailsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LeadGuestDetailsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setGuestDetailsFormDataInput(List<GuestDetailsFormDataInput> guestDetailsFormDataInputs) {
        removeAllViews();

        for (int i = 0; i < guestDetailsFormDataInputs.size(); i++) {
            TextView room = new TextView(getContext());
            LinearLayout.LayoutParams layoutParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
            room.setLayoutParams(layoutParams);
            room.setTextColor(ContextCompat.getColor(getContext(), R.color.teal));
            room.setTextAppearance(getContext(), R.style.Header4);
            addView(room);

            TextView name = new TextView(getContext());
            name.setId(R.id.tv_lead_guest_name);
            layoutParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics()));
            name.setLayoutParams(layoutParams);
            name.setTextAppearance(getContext(), R.style.Body);
            addView(name);

            TextView email = new TextView(getContext());
            email.setId(R.id.tv_lead_guest_email);
            layoutParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
            email.setLayoutParams(layoutParams);
            email.setTextAppearance(getContext(), R.style.Body);
            addView(email);

            room.setText(getContext().getString(R.string.review_booking_room, i + 1));
            GuestDetailsFormDataInput guestDetails = guestDetailsFormDataInputs.get(i);
            name.setText(getContext().getString(R.string.review_booking_name,
                    guestDetails.title(), guestDetails.firstName(), guestDetails.lastName()));
            email.setText(guestDetails.email());
        }
    }
}
