package com.whitbread.premierinn.editguest;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.jakewharton.rxbinding3.view.RxView;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.databinding.ActivityEditGuestBinding;
import com.whitbread.premierinn.guestdetails.adapter.RoomGuestDetailsData;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput;

import java.util.List;

import io.reactivex.Observable;
import kotlin.Unit;

public class EditGuestViewContainer extends ViewContainer implements EditGuestPresenter.View {

    private ActivityEditGuestBinding binding;

    public EditGuestViewContainer(@NonNull BaseActivity activity, ActivityEditGuestBinding binding) {
        super(activity);
        this.binding = binding;
        activity.setToolbar(activity.getString(R.string.edit_guest_details_title), true);
    }

    @Override
    public void displayGuestsForm(@NonNull List<GuestDetailsFormDataInput> guestDetailsFormDataInputs, boolean bookerIsStaying) {
        binding.tvEditGuestLeadGuestDetailsLabel.setText(getActivity().getString(R.string.lead_guest_details));
        binding.gdfvEditGuestBookerGuestForm.setVisibility(View.GONE);
        binding.rgdvEditGuestRooms.setup(guestDetailsFormDataInputs, true, bookerIsStaying);
    }

    @Override
    public void displayBookerAndGuestsForm(@NonNull GuestDetailsFormDataInput bookerDetails,
                                           @NonNull List<GuestDetailsFormDataInput> guestDetailsFormDataInputs) {
        binding.tvEditGuestLeadGuestDetailsLabel.setText(getActivity().getString(R.string.review_booking_your_details));
        binding.gdfvEditGuestBookerGuestForm.setValue(bookerDetails, false);
        binding.rgdvEditGuestRooms.setup(guestDetailsFormDataInputs, true, false);
    }

    @Override
    public Observable<GuestDetailsFormDataOutput> getBookerForm() {
        return binding.gdfvEditGuestBookerGuestForm.getFormWithTextAndFocus();
    }

    @Override
    public Observable<RoomGuestDetailsData> getRoomFormWithTextAndFocus() {
        return binding.rgdvEditGuestRooms.getFormWithTextAndFocus();
    }

    @Override
    public void showBookerValidationError(boolean showError, GuestDetailsFormDataOutput.Form form) {
        binding.gdfvEditGuestBookerGuestForm.showFormValidationError(showError, form);
    }

    @Override
    public void updateRoomFormList(GuestDetailsFormDataInput guestDetailsMutated, int position) {
        binding.rgdvEditGuestRooms.update(guestDetailsMutated, position);
    }

    @Override
    public Observable<Unit> getOnUpdateClick() {
        return RxView.clicks(binding.cabEditGuestUpdateDetails);
    }

    @Override
    public void startReviewBookingActivity(@NonNull EditGuestInput editGuestInput) {
        Intent intent = new Intent();
        intent.putExtra(EditGuestActivity.EDIT_GUEST_INPUT_KEY, editGuestInput);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().onBackPressed();
    }
}
