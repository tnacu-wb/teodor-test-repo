package com.whitbread.premierinn.hoteldetails.viewholder;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;

import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.databinding.ViewAccessibilityInfoPanelBinding;
import com.whitbread.premierinn.databinding.ViewCoronavirusLayoutBinding;
import com.whitbread.premierinn.databinding.ViewHotelAccessibilitySectionBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsCallUsBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsCheckInOutBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsDateGuestBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsFacilitiesBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsFullyBookedBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsGalleryBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsHeaderBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsHeadingBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsImportantInfoButtonBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsMapComponentBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsRateLoadingBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsRatePlanBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsRestaurantBarBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsTabbedContentBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsTextviewImageContentBinding;
import com.whitbread.premierinn.databinding.ViewHotelFacilitiesSectionBinding;
import com.whitbread.premierinn.databinding.ViewHotelParkingSectionBinding;
import com.whitbread.premierinn.databinding.ViewRoomSubstitutionInfoBinding;
import com.whitbread.premierinn.databinding.ViewRoomTypesSectionBinding;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.uimodel.AccessibilityInformationUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.HotelAccessibilityUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.CallUsUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.CheckInOutUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.CoronavirusUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.DateGuestUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.DiscountCodeUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.FacilitiesUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.FullyBookedUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.HotelParkingUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.RestaurantBarUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.HeaderUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.HeadingUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.HotelFacilitiesUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.HotelGalleryUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.ImportantInfoUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.MapUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.PageInfoUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.RateLoadingUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.RoomRatesUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.RoomSubstitutionUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.RoomTypesSectionUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.TabbedContentUiModel;

public class HotelDetailsViewHolderFactory {

    public static BaseRecyclerViewHolder createVHolder(@NonNull ViewGroup parent, @LayoutRes int viewType, PublishRelay<Object> relay) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());

        switch (viewType) {
            default:
            case HeadingUiModel.LAYOUT_TYPE:
                return new HeadingViewHolder(ViewHotelDetailsHeadingBinding.inflate(layoutInflater, parent, false), relay);
            case RestaurantBarUiModel.LAYOUT_TYPE:
                return new RestaurantBarViewHolder(
                        ViewHotelDetailsRestaurantBarBinding.inflate(layoutInflater, parent, false)
                );
            case HeaderUiModel.LAYOUT_TYPE:
                return new HeaderViewHolder(ViewHotelDetailsHeaderBinding.inflate(layoutInflater, parent, false));
            case CoronavirusUiModel.LAYOUT_TYPE:
                return new CoronavirusViewHolder(ViewCoronavirusLayoutBinding.inflate(layoutInflater, parent, false), relay);
            case TabbedContentUiModel.LAYOUT_TYPE:
                return new PageInfoViewPagerWithTabsViewHolder(ViewHotelDetailsTabbedContentBinding.inflate(layoutInflater, parent, false));
            case PageInfoUiModel.LAYOUT_TYPE:
                return new PageInfoViewHolder(ViewHotelDetailsTextviewImageContentBinding.inflate(layoutInflater, parent, false));
            case FacilitiesUiModel.LAYOUT_TYPE:
                return new FacilitiesViewHolder(
                        ViewHotelDetailsFacilitiesBinding.inflate(layoutInflater, parent, false), relay);
            case CallUsUiModel.LAYOUT_TYPE:
                return new CallUsViewHolderCard(
                        ViewHotelDetailsCallUsBinding.inflate(layoutInflater, parent, false)
                );
            case MapUiModel.LAYOUT_TYPE:
                return new MapViewHolder(ViewHotelDetailsMapComponentBinding.inflate(layoutInflater, parent, false), relay);
            case ImportantInfoUiModel.LAYOUT_TYPE:
                return new ImportantInfoViewHolder(
                        ViewHotelDetailsImportantInfoButtonBinding.inflate(layoutInflater, parent, false), relay
                );
            case DiscountCodeUiModel.LAYOUT_TYPE:
                ComposeView composeView =
                        (ComposeView) layoutInflater.inflate(DiscountCodeUiModel.LAYOUT_TYPE, parent, false);
                return new DiscountCodeViewHolder(composeView, relay);
            case RoomRatesUiModel.LAYOUT_TYPE:
                return new RatePlanViewHolder(
                        ViewHotelDetailsRatePlanBinding.inflate(layoutInflater, parent, false), relay
                );
            case RateLoadingUiModel.LAYOUT_TYPE:
                return new RateLoadingViewHolder(ViewHotelDetailsRateLoadingBinding.inflate(layoutInflater, parent, false));
            case FullyBookedUiModel.LAYOUT_TYPE:
                return new FullyBookedViewHolder(ViewHotelDetailsFullyBookedBinding.inflate(layoutInflater, parent, false), relay);
            case RoomSubstitutionUiModel.LAYOUT_TYPE:
                return new RoomSubstitutionViewHolder(ViewRoomSubstitutionInfoBinding.inflate(layoutInflater, parent, false));
            case AccessibilityInformationUiModel.LAYOUT_TYPE:
                return new AccessibilityViewHolder(ViewAccessibilityInfoPanelBinding.inflate(layoutInflater, parent, false), relay);
            case CheckInOutUiModel.LAYOUT_TYPE:
                return new CheckInOutViewHolder(
                        ViewHotelDetailsCheckInOutBinding.inflate(layoutInflater, parent, false)
                );
            case HotelFacilitiesUiModel.LAYOUT_TYPE:
                return new HotelFacilitiesSectionViewHolder(
                        ViewHotelFacilitiesSectionBinding.inflate(layoutInflater, parent, false), relay);
            case RoomTypesSectionUiModel.LAYOUT_TYPE:
                return new RoomTypesSectionViewHolder(
                        ViewRoomTypesSectionBinding.inflate(layoutInflater, parent, false), relay);
            case DateGuestUiModel.LAYOUT_TYPE:
                return new DateGuestViewHolder(
                        ViewHotelDetailsDateGuestBinding.inflate(layoutInflater, parent, false), relay);
            case HotelGalleryUiModel.LAYOUT_TYPE:
                return new HotelGalleryViewHolder(
                        ViewHotelDetailsGalleryBinding.inflate(layoutInflater, parent, false));
            case HotelParkingUiModel.LAYOUT_TYPE:
                return new  HotelParkingSectionViewHolder(
                        ViewHotelParkingSectionBinding.inflate(layoutInflater, parent, false), relay);
            case HotelAccessibilityUiModel.LAYOUT_TYPE:
                return new  HotelAccessibilitySectionViewHolder(
                        ViewHotelAccessibilitySectionBinding.inflate(layoutInflater, parent, false), relay);
        }
    }
}
