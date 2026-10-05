package com.whitbread.premierinn.hoteldetails.uimodel;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.RoomSubstitutionInfo;
import com.whitbread.premierinn.hoteldetails.UiModelListItem;

import java.util.List;

@AutoValue
public abstract class RoomSubstitutionUiModel implements UiModelListItem<RoomSubstitutionUiModel> {

    public static final int LAYOUT_TYPE = R.layout.view_room_substitution_info;

    public abstract int order();

    public abstract List<RoomSubstitutionInfo> roomSubstitutionInfoList();

    @Override
    public void bind(BaseRecyclerViewHolder<RoomSubstitutionUiModel> viewHolder) {
        viewHolder.bind(this);
    }

    @Override
    public int getLayout() {
        return LAYOUT_TYPE;
    }

    @Override
    public int listOrder() {
        return order();
    }

    public static Builder builder() {
        return new AutoValue_RoomSubstitutionUiModel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder order(int order);

        public abstract Builder roomSubstitutionInfoList(List<RoomSubstitutionInfo> list);

        public abstract RoomSubstitutionUiModel build();
    }
}