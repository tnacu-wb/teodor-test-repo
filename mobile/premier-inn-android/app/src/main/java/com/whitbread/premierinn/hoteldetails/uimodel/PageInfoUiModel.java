package com.whitbread.premierinn.hoteldetails.uimodel;

import androidx.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.Content;
import com.whitbread.premierinn.hoteldetails.UiModelListItem;

import java.util.List;


@AutoValue
public abstract class PageInfoUiModel implements UiModelListItem<PageInfoUiModel> {

    public static final int LAYOUT_TYPE = R.layout.view_hotel_details_textview_image_content;

    public abstract List<Content> contents();

    public abstract int order();

    @Nullable
    public abstract String imageUrl();

    public abstract Type textAlignment();

    @Override
    public void bind(BaseRecyclerViewHolder<PageInfoUiModel> viewHolder) {
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
        return new AutoValue_PageInfoUiModel.Builder().textAlignment(Type.LEFT_ALIGN);
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder order(int order);

        public abstract Builder contents(List<Content> contents);

        public abstract Builder textAlignment(Type textAlignment);

        public abstract Builder imageUrl(String imageUrl);

        public abstract PageInfoUiModel build();
    }

    public enum Type {
        LEFT_ALIGN, CENTER_ALIGN
    }

}