package com.whitbread.premierinn.importanthotelinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.whitbread.premierinn.api.response.booking.BookingNote;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivityImportantHotelInformationBinding;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.InfoItem;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ImportantHotelInfoActivity extends BasePresenterActivity<ImportantHotelInfoPresenter.View,
        ActivityImportantHotelInformationBinding, ImportantHotelInfoPresenter> {

        // TODO: Remove this and associated files, this has been replaced with ImportantInfoBottomSheetFragment
    private static final String IMPORTANT_HOTEL_INFO_INPUT_KEY = "important_hotel_info_input_key";
    private static final String IMPORTANT_HOTEL_INFO_INPUT_KEY_OPERA = "important_hotel_info_input_key_opera";
    private static final String IMPORTANT_HOTEL_INFO_ARRIVAL_KEY = "important_hotel_info_arrival_key";
    private static final String IMPORTANT_HOTEL_INFO_DEPARTURE_KEY = "important_hotel_info_departure_key";

    @Inject
    ImportantHotelInfoPresenter presenter;

    @NonNull
    @Override
    protected ActivityImportantHotelInformationBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityImportantHotelInformationBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static void start(@NonNull Context context, List<BookingNote> notes, List<InfoItem> infoItems,
                             LocalDate arrivalDate, LocalDate departureDate) {
        Intent intent = new Intent(context, ImportantHotelInfoActivity.class);
        ArrayList<BookingNote> bookingNotes = new ArrayList<>(notes);
        intent.putParcelableArrayListExtra(IMPORTANT_HOTEL_INFO_INPUT_KEY, bookingNotes);
        ArrayList<InfoItem> infoItemsList = new ArrayList<>(infoItems);
        intent.putExtra(IMPORTANT_HOTEL_INFO_INPUT_KEY_OPERA, infoItemsList);
        intent.putExtra(IMPORTANT_HOTEL_INFO_ARRIVAL_KEY, arrivalDate);
        intent.putExtra(IMPORTANT_HOTEL_INFO_DEPARTURE_KEY, departureDate);
        context.startActivity(intent);
    }

    @Override
    protected ImportantHotelInfoPresenter.@NotNull View provideView() {
        return new ImportantHotelInfoViewContainer(this, binding);
    }

    @Override
    protected @NotNull ImportantHotelInfoPresenter createPresenter() {
        presenter.initParams(
                Objects.requireNonNull(getIntent().getParcelableArrayListExtra(IMPORTANT_HOTEL_INFO_INPUT_KEY)),
                (ArrayList<InfoItem>) getIntent().getSerializableExtra(IMPORTANT_HOTEL_INFO_INPUT_KEY_OPERA),
                (LocalDate) getIntent().getSerializableExtra(IMPORTANT_HOTEL_INFO_ARRIVAL_KEY),
                (LocalDate) getIntent().getSerializableExtra(IMPORTANT_HOTEL_INFO_DEPARTURE_KEY)
        );
        return presenter;
    }
}