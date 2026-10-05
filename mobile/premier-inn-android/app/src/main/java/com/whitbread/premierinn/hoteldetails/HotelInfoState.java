package com.whitbread.premierinn.hoteldetails;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.Urls;
import com.whitbread.premierinn.api.response.availability.HotelImage;
import com.whitbread.premierinn.api.response.availability.HotelInfo;

import java.util.List;

import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

@AutoValue
public abstract class HotelInfoState {

    public static final Function<Pair<HotelDetailsInput, HotelInfo>, HotelInfoState> MAPPER = pair -> {
        // input.hotelImageUrl() == null aka from direct hotel search
        HotelInfo hotelInfo = pair.second;
        HotelDetailsInput input = pair.first;
        if (input.hotelImageUrl() != null) {
            return HotelInfoState.builder().action(Action.SUCCESS_HOTEL_INFO_FROM_SEARCH_RESULTS).hotelInfo(hotelInfo).build();
        } else {
            return HotelInfoState.builder().action(Action.SUCCESS_HOTEL_INFO).hotelInfo(hotelInfo).build();
        }
    };
    public static final Function<HotelInfo, HotelInfoState> MAPPER2 = info -> {
        return HotelInfoState.builder().action(Action.SUCCESS_HOTEL_INFO).hotelInfo(info).build();
    };

    public static ObservableTransformer<HotelInfoState, List<HotelImage>> transformToHotelImageList(Predicate<Pair<HotelImage,
            String>> tagFilterType) {
        return hotelSingle -> hotelSingle
                .filter(allowIfHotelInfoStateIsSuccessful())
                .map(HotelInfoState::hotelInfo)
                .filter(allowIfImagesExist())
                .map(HotelInfo::images)
                .flatMapIterable(images -> images)
                .filter(allowIfTagsExist())
                .flatMapIterable((imageItem) -> imageItem.tags(), Pair::new)
                .filter(tagFilterType)
                .map(pair -> pair.first)
                .distinct()
                .toList().toObservable();
    }

    public static ObservableTransformer<HotelInfoState, List<String>> transformToImageUrlsList(Predicate<Pair<HotelImage,
            String>> tagFilterType) {
        return hotelSingle -> hotelSingle
                .filter(allowIfHotelInfoStateIsSuccessful())
                .map(HotelInfoState::hotelInfo)
                .filter(allowIfImagesExist())
                .map(HotelInfo::images)
                .flatMapIterable(images -> images)
                .filter(allowIfTagsExist())
                .flatMapIterable((imageItem) -> imageItem.tags(), Pair::new)
                .filter(tagFilterType)
                .map(pair -> Urls.CONTENT_BASE_URL + pair.first.fileReference())
                .distinct()
                .toList()
                .toObservable();
    }

    @Nullable
    public abstract HotelInfo hotelInfo();

    public abstract Action action();

    public static Builder builder() {
        return new AutoValue_HotelInfoState.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder hotelInfo(HotelInfo hotelInfo);

        public abstract Builder action(Action action);

        public abstract HotelInfoState build();
    }

    @NonNull
    private static Predicate<HotelImage> allowIfTagsExist() {
        return image -> image.tags() != null;
    }

    @NonNull
    private static Predicate<HotelInfo> allowIfImagesExist() {
        return hotel -> hotel.images() != null;
    }

    @NonNull
    public static Predicate<HotelInfoState> allowIfHotelInfoStateIsSuccessful() {
        return state -> state.action() == HotelInfoState.Action.SUCCESS_HOTEL_INFO_FROM_SEARCH_RESULTS
                || state.action() == HotelInfoState.Action.SUCCESS_HOTEL_INFO;
    }

    public enum Action {
        SUCCESS_HOTEL_INFO,
        SUCCESS_HOTEL_INFO_FROM_SEARCH_RESULTS,
        LOADING,
        ERROR
    }
}
