package com.whitbread.premierinn.importanthotelinfo;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.whitbread.premierinn.api.response.booking.BookingNote;
import com.whitbread.premierinn.databinding.ItemBookingNoteBinding;

import java.util.ArrayList;
import java.util.List;

//TODO: Will be removed as a part of Bart Decomm, no need to migrate from ButterKnife to ViewBinding
public class BookingNotesAdapter extends RecyclerView.Adapter<BookingNotesAdapter.BookingNotesHolder> {
    private ItemBookingNoteBinding binding;
    private final List<BookingNote> notes;

    public BookingNotesAdapter(@NonNull List<BookingNote> notes) {
        this.notes = new ArrayList<>(notes);
    }

    @Override
    public BookingNotesHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        binding = ItemBookingNoteBinding.inflate(layoutInflater, viewGroup, false);

        return new BookingNotesHolder(binding);
    }

    @Override
    public void onBindViewHolder(BookingNotesHolder bookingNotesHolder, int position) {
        binding.tvImportantHotelInfoNoteDescription.setText(notes.get(position).text());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class BookingNotesHolder extends RecyclerView.ViewHolder {

        BookingNotesHolder(ItemBookingNoteBinding binding) {
            super(binding.getRoot());
        }
    }
}
