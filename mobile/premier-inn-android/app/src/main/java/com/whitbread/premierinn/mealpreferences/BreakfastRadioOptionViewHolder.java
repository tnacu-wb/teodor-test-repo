package com.whitbread.premierinn.mealpreferences;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class BreakfastRadioOptionViewHolder extends RecyclerView.ViewHolder {

    private final BreakfastRadioButtonView breakfastRadioButtonView;

    BreakfastRadioOptionViewHolder(@NonNull BreakfastRadioButtonView breakfastRadioButtonView) {
        super(breakfastRadioButtonView);
        this.breakfastRadioButtonView = breakfastRadioButtonView;
    }

    public void bind(@NonNull BreakfastRadioButtonInput breakfastRadioButtonInput) {
        breakfastRadioButtonView.setContent(breakfastRadioButtonInput);
        breakfastRadioButtonView.setChecked(breakfastRadioButtonInput.isSelected());
    }
}
