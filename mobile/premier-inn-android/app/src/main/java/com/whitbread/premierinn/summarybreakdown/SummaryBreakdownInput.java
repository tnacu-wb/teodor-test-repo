package com.whitbread.premierinn.summarybreakdown;

import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.ParcelablePrice;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem;
import com.whitbread.premierinn.summary.SummaryInput;

import java.util.List;

@AutoValue
public abstract class SummaryBreakdownInput implements Parcelable {

    public static SummaryBreakdownInput create(@NonNull SummaryInput summaryInput,
                                               float totalUpsellPrice,
                                               @Nullable List<UpsellItem> selectedUpsells,
                                               @Nullable List<ParcelableExtrasItem> selectedExtras,
                                               @NonNull ParcelablePrice totalStayPrice,
                                               boolean isBusiness) {
        return create(summaryInput, totalUpsellPrice, selectedUpsells, selectedExtras, totalStayPrice, isBusiness, 0);
    }

    public static SummaryBreakdownInput create(@NonNull SummaryInput summaryInput,
                                               float totalUpsellPrice,
                                               @Nullable List<UpsellItem> selectedUpsells,
                                               @Nullable List<ParcelableExtrasItem> selectedExtras,
                                               @NonNull ParcelablePrice totalStayPrice,
                                               boolean isBusiness,
                                               int totalChildrenBreakfast) {
        return new AutoValue_SummaryBreakdownInput(summaryInput, totalUpsellPrice, selectedUpsells,
                selectedExtras, totalStayPrice, isBusiness, totalChildrenBreakfast);
    }

    public abstract SummaryInput summaryInput();

    public abstract float totalUpsellPrice();

    @Nullable
    public abstract List<UpsellItem> selectedUpsells();

    @Nullable
    public abstract List<ParcelableExtrasItem> selectedExtras();

    public abstract ParcelablePrice totalStayPrice();

    public abstract boolean isBusinessTrip();

    public abstract int totalChildrenBreakfast();
}