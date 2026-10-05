package com.whitbread.premierinn.hoteldetails.viewholder;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.databinding.ViewHotelDetailsImportantInfoButtonBinding;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.event.ImportantInfoClickEvent;
import com.whitbread.premierinn.hoteldetails.uimodel.ImportantInfoUiModel;


class ImportantInfoViewHolder extends BaseRecyclerViewHolder<ImportantInfoUiModel> {
    private final ViewHotelDetailsImportantInfoButtonBinding binding;

    ImportantInfoViewHolder(ViewHotelDetailsImportantInfoButtonBinding binding, PublishRelay<Object> relay) {
        super(binding.getRoot());
        this.binding = binding;
        RxView.clicks(binding.hotelDetailsImportantInfoButton)
                .subscribe(o -> relay.accept(new ImportantInfoClickEvent(getAdapterPosition())));
    }

    @Override
    public void bind(ImportantInfoUiModel item) {
        binding.importantInfoLabel.setText(
                String.format(
                        itemView.getContext().getString(R.string.hotel_details_important_hotel_info_label_numbered),
                        item.count()
                )
        );
    }
}