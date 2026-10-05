package com.whitbread.premierinn.search.adapter;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.databinding.ItemLocationSearchListBinding;
import com.whitbread.premierinn.databinding.ItemSearchEndBinding;
import com.whitbread.premierinn.databinding.ItemSearchHeaderListBinding;
import com.whitbread.premierinn.databinding.ItemSearchListBinding;
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.search.adapter.adapteritem.CurrentLocationSearchItemUiModel;
import com.whitbread.premierinn.search.adapter.adapteritem.EndOfResultsUiModel;
import com.whitbread.premierinn.search.adapter.adapteritem.HeaderUiModel;
import com.whitbread.premierinn.search.adapter.adapteritem.SearchAdapterItem;
import com.whitbread.premierinn.search.adapter.adapteritem.SearchItemUiModel;
import com.whitbread.premierinn.search.adapter.adapteritem.viewholder.EndOfResultsHolder;
import com.whitbread.premierinn.search.adapter.adapteritem.viewholder.HeaderSearchItemHolder;
import com.whitbread.premierinn.search.adapter.adapteritem.viewholder.LocationItemViewHolder;
import com.whitbread.premierinn.search.adapter.adapteritem.viewholder.SearchItemViewHolder;

import io.reactivex.Observable;

import static com.whitbread.premierinn.common.utils.ViewUtils.allowIfValidPosition;

public class SearchAdapter extends ListAdapter<SearchAdapterItem, BaseRecyclerViewHolder<SearchAdapterItem>> {

    private final PublishRelay<Object> locationClickRelay = PublishRelay.create();
    private final PublishRelay<SearchSuggetionItem> onSearchItemClickRelay = PublishRelay.create();
    private final PublishRelay<Object> onClearClickRelay = PublishRelay.create();
    private final LogService crashlyticsLog = new LogService();

    public SearchAdapter() {
        super(DIFF_CALLBACK);
    }

    @Override
    public BaseRecyclerViewHolder<SearchAdapterItem> onCreateViewHolder(ViewGroup parent, int layoutResId) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        BaseRecyclerViewHolder holder = null;
        switch (layoutResId) {
            case CurrentLocationSearchItemUiModel.LAYOUT_ID:
                ItemLocationSearchListBinding itemLocationSearchListBinding =
                        ItemLocationSearchListBinding.inflate(inflater, parent, false);
                holder = new LocationItemViewHolder(itemLocationSearchListBinding);

                RxView.clicks(itemLocationSearchListBinding.getRoot()).
                        subscribe(__ -> locationClickRelay.accept(new Object()));
                break;
            case SearchItemUiModel.LAYOUT_ID:
                ItemSearchListBinding itemSearchListBinding = ItemSearchListBinding.inflate(inflater, parent, false);
                holder = new SearchItemViewHolder(itemSearchListBinding);
                RecyclerView.ViewHolder finalHolder = holder;

                RxView.clicks(itemSearchListBinding.getRoot())
                        .map(__ -> finalHolder.getAdapterPosition())
                        .filter(allowIfValidPosition())
                        .map(adapterPosition -> ((SearchItemUiModel) getItem(adapterPosition)).searchItem())
                        .subscribe(onSearchItemClickRelay::accept);
                break;
            case HeaderUiModel.LAYOUT_ID:
                ItemSearchHeaderListBinding  itemSearchHeaderListBinding = ItemSearchHeaderListBinding.inflate(inflater, parent, false);
                holder = new HeaderSearchItemHolder(itemSearchHeaderListBinding);

                RxView.clicks(itemSearchHeaderListBinding.tvSearchHeaderClear)
                        .subscribe(onClearClickRelay::accept);
                break;
            case EndOfResultsUiModel.LAYOUT_ID:
                ItemSearchEndBinding itemSearchEndBinding = ItemSearchEndBinding.inflate(inflater, parent, false);
                holder = new EndOfResultsHolder(itemSearchEndBinding);
                break;
            default:
                crashlyticsLog.log("Wrong layout resource id, must be one of the declared onCreateViewHolder");
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder<SearchAdapterItem> holder, int position) {
        getItem(position).onBind(holder);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getLayoutId();
    }

    public Observable<Object> onSearchLocationClick() {
        return locationClickRelay;
    }

    public Observable<SearchSuggetionItem> onSearchItemClick() {
        return onSearchItemClickRelay;
    }

    public Observable<Object> onClearClicked() {
        return onClearClickRelay;
    }

    private static final DiffUtil.ItemCallback<SearchAdapterItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<SearchAdapterItem>() {
        @Override public boolean areItemsTheSame(SearchAdapterItem oldItem, SearchAdapterItem newItem) {
            return oldItem.toString().equals(newItem.toString());
        }

        @Override public boolean areContentsTheSame(SearchAdapterItem oldItem, SearchAdapterItem newItem) {
            return oldItem.equals(newItem);
        }
    };
}
