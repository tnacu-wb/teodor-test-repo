package com.whitbread.premierinn.mealpreferences;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.jakewharton.rxbinding3.view.RxView;

import java.util.ArrayList;
import java.util.List;


class BreakfastOptionsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<BreakfastRadioButtonInput> breakfastContents;

    BreakfastOptionsListAdapter(List<BreakfastRadioButtonInput> breakfastContents) {
        this.breakfastContents = breakfastContents;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BreakfastRadioButtonView view = new BreakfastRadioButtonView(parent.getContext());
        BreakfastRadioOptionViewHolder viewHolder = new BreakfastRadioOptionViewHolder(view);

        RxView.clicks(view)
                .map(__ -> breakfastContents.get(viewHolder.getAdapterPosition()))
                .subscribe(breakfastInput -> selectItem(breakfastInput.code()));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BreakfastRadioOptionViewHolder) holder).bind(breakfastContents.get(position));
    }

    @Override
    public int getItemCount() {
        return breakfastContents.size();
    }

    void selectItem(@NonNull String upsellItemCode) {
        List<BreakfastRadioButtonInput> newList = new ArrayList<>();
        for (BreakfastRadioButtonInput breakfast : breakfastContents) {
            if (upsellItemCode.equals(breakfast.code())) {
                breakfast = breakfast.toBuilder().isSelected(true).build();
            } else {
                breakfast = breakfast.toBuilder().isSelected(false).build();
            }
            newList.add(breakfast);
        }
        breakfastContents.clear();
        breakfastContents.addAll(newList);
        notifyDataSetChanged();
    }

    public BreakfastRadioButtonInput getSelectedItem() {
        for (BreakfastRadioButtonInput breakfast : breakfastContents) {
           if (breakfast.isSelected()) {
               return breakfast;
           }
        }
        return null;
    }

    public BreakfastRadioButtonInput getItemAtPosition(int position) {
        return breakfastContents.get(position);
    }
}
