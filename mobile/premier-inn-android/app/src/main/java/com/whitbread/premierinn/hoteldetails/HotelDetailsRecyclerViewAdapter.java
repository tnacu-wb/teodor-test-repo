package com.whitbread.premierinn.hoteldetails;

import static com.whitbread.premierinn.hoteldetails.viewholder.HotelDetailsViewHolderFactory.createVHolder;

import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.hoteldetails.uimodel.HeaderUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.RestaurantBarUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.RoomRatesUiModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HotelDetailsRecyclerViewAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

    private List<UiModelListItem> items = new ArrayList<>();

    private final PublishRelay<Object> relay;

    private final LogService logService;

    public HotelDetailsRecyclerViewAdapter(PublishRelay<Object> relay, LogService logService) {
        this.relay = relay;
        this.logService = logService;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return createVHolder(parent, viewType, relay);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        try {
            items.get(position).bind(holder);
        } catch (ClassCastException e) {
            logService.logException(e, e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getLayout();
    }

    public void addItem(UiModelListItem item) {
        List<UiModelListItem> newList = getNewList(item, items);
        List<UiModelListItem> oldList = new ArrayList<>(items);

        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new UiModelListItemDiffCallback(oldList, newList));
        items = newList;
        Collections.sort(newList, (o1, o2) -> o1.listOrder() - o2.listOrder());

        diffResult.dispatchUpdatesTo(this);
    }

    private List<UiModelListItem> getNewList(@NonNull UiModelListItem itemToAdd, @NonNull List<UiModelListItem> adapterList) {
        List<UiModelListItem> listItems = new ArrayList<>(adapterList);
        int indexFound = sameItemFound(itemToAdd);
        if (indexFound != -1) {
            listItems.set(indexFound, itemToAdd);
        } else {
            listItems.add(itemToAdd);
        }
        return listItems;
    }

    private int sameItemFound(UiModelListItem item) {
        if (items == null || items.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).listOrder() == item.listOrder()) {
                return i;
            }
        }
        return -1;
    }

    public int getPositionOfHeading(@StringRes int stringRes) {
        for (int i = 0; i < items.size(); i++) {
            if ((items.get(i) instanceof HeaderUiModel
                    && ((HeaderUiModel) items.get(i)).headingStringRes() == stringRes)
                    || (items.get(i) instanceof RestaurantBarUiModel
                    && ((RestaurantBarUiModel) items.get(i)).headingStringRes() == stringRes)
            ) {
                return i;
            }
        }
        return -1;
    }

    public int getRatesPlanPosition() {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) instanceof RoomRatesUiModel) {
                return i;
            }
        }
        return -1;
    }

    public UiModelListItem getItem(int position) {
        return items.get(position);
    }

    public List<UiModelListItem> getItems() {
        return items;
    }

    public boolean itemViewTypeExists(@LayoutRes int type) {
        for (UiModelListItem item : items) {
            if (type == item.getLayout()) {
                return true;
            }
        }
        return false;
    }

    public boolean itemViewAboveViewport(@LayoutRes int type, int firstOnScreen) {
        for (int i = 0; i < firstOnScreen; i++) {
            if (type == items.get(i).getLayout()) {
                return true;
            }
        }
        return false;
    }

    public void removeItem(int position) {
        items.remove(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class UiModelListItemDiffCallback extends DiffUtil.Callback {

        private final List<UiModelListItem> oldList;
        private final List<UiModelListItem> newList;

        UiModelListItemDiffCallback(List<UiModelListItem> oldList, List<UiModelListItem> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).listOrder() == newList.get(newItemPosition).listOrder();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return !oldList.isEmpty() && !newList.isEmpty() && oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}
