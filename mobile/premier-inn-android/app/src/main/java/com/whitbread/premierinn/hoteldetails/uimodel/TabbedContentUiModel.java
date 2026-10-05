package com.whitbread.premierinn.hoteldetails.uimodel;

import androidx.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.Content;
import com.whitbread.premierinn.hoteldetails.UiModelListItem;

import java.util.List;

@AutoValue
public abstract class TabbedContentUiModel implements UiModelListItem<TabbedContentUiModel> {

    public static final int LAYOUT_TYPE = R.layout.view_hotel_details_tabbed_content;

    public abstract List<String> tabTitles();

    public abstract List<List<Content>> tabContentList();

    public abstract int order();

    @Nullable
    public abstract String imageUrl();

    public abstract boolean hubStyle();

    @Override
    public void bind(BaseRecyclerViewHolder<TabbedContentUiModel> viewHolder) {
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
        return new AutoValue_TabbedContentUiModel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder order(int order);

        public abstract Builder tabTitles(List<String> tabTitles);

        public abstract Builder tabContentList(List<List<Content>> tabContentList);

        public abstract Builder imageUrl(String imageUrl);

        public abstract Builder hubStyle(boolean hubStyle);

        public abstract TabbedContentUiModel build();
    }
}