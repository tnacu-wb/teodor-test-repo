package com.whitbread.premierinn.guestdetails.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

import com.contentsquare.android.Contentsquare;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormView;

import java.util.List;

import io.reactivex.Observable;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class RoomGuestDetailsView extends LinearLayout {

    private static final int BOOKER_TOP_MARGIN_DP = 12;
    private static final int BOOKER_NAME_BOTTOM_MARGIN_DP = 8;
    private static final int BOOKER_SECTION_BOTTOM_MARGIN_DP = 36;
    private static final int VIEWS_PER_EDITABLE_GUEST = 2;
    private static final int ROOM_NUM_HEADING_VIEW_COUNT = 1;
    private static final int UNEDITABLE_BOOKER_VIEW_COUNT = 1;

    private PublishRelay<RoomGuestDetailsData> recyclerPublishSubject;
    private boolean bookerIsStaying;
    private boolean bookerIsEditable;

    public RoomGuestDetailsView(Context context) {
        super(context);
        init();
    }

    public RoomGuestDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoomGuestDetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.recyclerPublishSubject = PublishRelay.create();
        setOrientation(VERTICAL);
    }

    public Observable<RoomGuestDetailsData> getFormWithTextAndFocus() {
        return recyclerPublishSubject;
    }

    public void setup(List<GuestDetailsFormDataInput> items, boolean bookerIsEditable, boolean bookerIsStaying) {
        removeAllViews();
        this.bookerIsEditable = bookerIsEditable;
        this.bookerIsStaying = bookerIsStaying;
        final float density = getContext().getResources().getDisplayMetrics().density;

        for (int roomIndex = 0; roomIndex < items.size(); roomIndex++) {
            GuestDetailsFormDataInput guestDetailsFormDataInput = items.get(roomIndex);

            TextView roomIndicator = new TextView(new ContextThemeWrapper(getContext(), R.style.BodySemiBold));
            roomIndicator.setTextColor(ContextCompat.getColor(getContext(), R.color.teal));
            roomIndicator.setText(getResources().getString(R.string.guest_details_room, roomIndex + 1));
            addView(roomIndicator);


            if (guestIsBooker(roomIndex, bookerIsStaying) && !bookerIsEditable) {
                TextView tvBookerDetailsName = new TextView(new ContextThemeWrapper(getContext(), R.style.Body));
                LinearLayout.LayoutParams bookerNameLayoutParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                bookerNameLayoutParams.setMargins(0, (int) (BOOKER_TOP_MARGIN_DP * density), 0,
                        (int) (BOOKER_NAME_BOTTOM_MARGIN_DP * density));
                tvBookerDetailsName.setLayoutParams(bookerNameLayoutParams);
                GuestDetailsFormDataInput bookerInput = items.get(0);
                tvBookerDetailsName.setText(getContext().getString(R.string.guest_details_booker_detail_format, bookerInput.title(),
                        bookerInput.firstName(), bookerInput.lastName()));
                addView(tvBookerDetailsName);

                TextView tvBookerDetailsEmail = new TextView(new ContextThemeWrapper(getContext(), R.style.Body));
                LinearLayout.LayoutParams bookerEmailLayoutParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                bookerEmailLayoutParams.setMargins(0, 0, 0, (int) (BOOKER_SECTION_BOTTOM_MARGIN_DP * density));
                tvBookerDetailsEmail.setLayoutParams(bookerEmailLayoutParams);
                tvBookerDetailsEmail.setText(bookerInput.email());
                addView(tvBookerDetailsEmail);

                Contentsquare.mask(tvBookerDetailsName);
                Contentsquare.mask(tvBookerDetailsEmail);
            } else {
                GuestDetailsFormView guestDetailsFormView = new GuestDetailsFormView(getContext());
                LinearLayout.LayoutParams guestLayoutParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                guestLayoutParams.setMargins(0, 0, 0, (int) (BOOKER_SECTION_BOTTOM_MARGIN_DP * density));
                guestDetailsFormView.setLayoutParams(guestLayoutParams);
                guestDetailsFormView.setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
                guestDetailsFormView.setFocusableInTouchMode(true);
                guestDetailsFormView.setValue(guestDetailsFormDataInput, false);
                if (!guestIsBooker(roomIndex, bookerIsStaying)) {
                    guestDetailsFormView.hideEmailField();
                }
                addView(guestDetailsFormView);
                final int finalRoomNumber = roomIndex;
                guestDetailsFormView.getFormWithTextAndFocus()
                        .takeUntil(RxView.detaches(guestDetailsFormView))
                        .map(formViewData -> RoomGuestDetailsData.create(formViewData, finalRoomNumber))
                        .skip(1)
                        .subscribe(recyclerPublishSubject);
            }
        }
    }

    private boolean guestIsBooker(int roomIndex, boolean bookerIsStaying) {
        return roomIndex == 0 && bookerIsStaying;
    }

    public void update(GuestDetailsFormDataInput guestDetailsFormDataInput, int position) {
        if (!guestIsBooker(position, bookerIsStaying) || (bookerIsStaying && bookerIsEditable)) {

            int viewPosition = ((position) * VIEWS_PER_EDITABLE_GUEST)
                    + ROOM_NUM_HEADING_VIEW_COUNT
                    + (bookerIsStaying && !bookerIsEditable ? UNEDITABLE_BOOKER_VIEW_COUNT : 0);

            GuestDetailsFormView guestDetailsFormView = (GuestDetailsFormView) getChildAt(viewPosition);
            guestDetailsFormView.setValue(guestDetailsFormDataInput, true);
        }
    }
}
