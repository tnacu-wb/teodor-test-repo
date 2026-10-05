package com.whitbread.premierinn.criteria.model;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.response.customer.RoomRequirements;
import com.whitbread.premierinn.criteria.view.NumberSelectorViewInput;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.Collections;
import java.util.List;

@AutoValue
public abstract class CriteriaState implements Parcelable {

    public static final int MIN_ALLOWED_NIGHTS = 1;
    public static final int MAX_ALLOWED_NIGHTS = 9;
    public static final int MAX_ROOMS = 4;

    public static CriteriaState create(String searchedText, LocalDate arrivalDate, NumberSelectorViewInput nights,
                                       List<RoomConfiguration> roomConfigurations, @Nullable TooManyRoomsMessage tooManyRoomsMessage) {
        return new AutoValue_CriteriaState(searchedText, arrivalDate, nights, roomConfigurations, tooManyRoomsMessage);
    }

    public static CriteriaState updateRoomConfigurations(CriteriaState state, List<RoomConfiguration> roomConfigurations) {
        return create(state.searchedText(), state.arrivalDate(), state.nights(), roomConfigurations, state.tooManyRoomsMessage());
    }

    public static CriteriaState updateTooManyRoomsMessage(CriteriaState state, @Nullable TooManyRoomsMessage tooManyRoomsMessage) {
        return create(state.searchedText(), state.arrivalDate(), state.nights(), state.roomConfigurations(), tooManyRoomsMessage);
    }

    public static CriteriaState createDefault(String searchedText) {
        return createFromRoomConfiguration(searchedText, RoomConfigurationCreator.INSTANCE.createDefault(false));
    }

    public static CriteriaState createFromRoomRequirements(String searchText, RoomRequirements roomRequirements) {
        return createFromRoomConfiguration(
                searchText,
                RoomConfigurationCreator.INSTANCE.createFromRoomRequirements(roomRequirements, false));
    }

    private static CriteriaState createFromRoomConfiguration(String searchText, RoomConfiguration configuration) {
        NumberSelectorViewInput numberSelectorInput = NumberSelectorViewInput.builder()
                .value(MIN_ALLOWED_NIGHTS)
                .plusEnabled(true).build();

        return create(searchText, LocalDate.now(), numberSelectorInput,
                Collections.singletonList(configuration), null);
    }

    public abstract String searchedText();

    public abstract LocalDate arrivalDate();

    public abstract NumberSelectorViewInput nights();

    public abstract List<RoomConfiguration> roomConfigurations();

    @Nullable
    public abstract TooManyRoomsMessage tooManyRoomsMessage();

    public int getMaxAllowedNightsWithOneYearLimit() {
        return getMaxAllowedNightsWithOneYearLimit(arrivalDate());
    }

    public int getMaxAllowedNightsWithOneYearLimit(@NonNull LocalDate arrivalDate) {
        LocalDate oneYearFromNow = LocalDate.now().plusYears(1);
        long numberOfNightsCalculated = ChronoUnit.DAYS.between(arrivalDate, oneYearFromNow);
        if (numberOfNightsCalculated > MAX_ALLOWED_NIGHTS) {
            return MAX_ALLOWED_NIGHTS;
        } else {
            return (int) numberOfNightsCalculated;
        }
    }

    public LocalDate getDepartureDate() {
        return arrivalDate().plusDays(nights().value());
    }

    public boolean canAddMoreRooms() {
        return roomConfigurations().size() < CriteriaState.MAX_ROOMS;
    }

}
