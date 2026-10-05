package com.whitbread.premierinn.importanthotelinfo;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whitbread.premierinn.databinding.ItemBookingNoteBinding;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.InfoItem;

import java.util.ArrayList;
import java.util.List;

public class ImportantInfoAdapter extends RecyclerView.Adapter<ImportantInfoAdapter.ImportantInfoHolder> {

    private final List<InfoItem> infoItems;
    private ItemBookingNoteBinding binding = null;

    public ImportantInfoAdapter(@NonNull List<InfoItem> infoItems) {
        this.infoItems = new ArrayList<>(infoItems);
    }

    @Override
    public ImportantInfoHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        binding = ItemBookingNoteBinding.inflate(inflater, viewGroup, false);
        return new ImportantInfoHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(ImportantInfoHolder importantInfoHolder, int position) {
        binding.tvImportantHotelInfoNoteDescription.setText(infoItems.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return infoItems.size();
    }

    static class ImportantInfoHolder extends RecyclerView.ViewHolder {

        ImportantInfoHolder(View itemView) {
            super(itemView);
        }
    }
}
