package com.whitbread.premierinn.importanthotelinfo;


import androidx.annotation.NonNull;

import com.jakewharton.rxbinding3.view.RxView;
import com.whitbread.premierinn.api.response.booking.BookingNote;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.databinding.ActivityImportantHotelInformationBinding;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.InfoItem;

import java.util.List;

import io.reactivex.Observable;
import kotlin.Unit;

public class ImportantHotelInfoViewContainer implements ImportantHotelInfoPresenter.View {

    private ActivityImportantHotelInformationBinding binding;
    private BaseActivity activity;

    public ImportantHotelInfoViewContainer(@NonNull BaseActivity activity,
                                           ActivityImportantHotelInformationBinding binding) {
        this.activity = activity;
        this.binding = binding;
    }

    @Override
    public void showBookingNotes(List<BookingNote> bookingNotes) {
        binding.rvImportantHotelInfoNotesList.setAdapter(new BookingNotesAdapter(bookingNotes));
    }

    @Override
    public void showImportantInfo(List<InfoItem> infoItems) {
        binding.rvImportantHotelInfoNotesList.setAdapter(new ImportantInfoAdapter(infoItems));
    }

    @Override
    public void closeScreen() {
        activity.finish();
    }

    @Override
    public Observable<Unit> onCrossClick() {
        return RxView.clicks(binding.ivImportantHotelInfoCloseIcon);
    }
}
