package com.whitbread.premierinn.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.databinding.ViewSummaryExpensesBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class SummaryExpensesView extends RelativeLayout {

    private ViewSummaryExpensesBinding binding;

    public SummaryExpensesView(Context context) {
        super(context);
        init(null);
    }

    public SummaryExpensesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SummaryExpensesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = ViewSummaryExpensesBinding.inflate(inflater, this, true);
        populateDefaultTitle(attrs);
    }

    private void populateDefaultTitle(@Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SummaryExpensesView);
        if (typedArray.hasValue(R.styleable.SummaryExpensesView_defaultTitle)) {
            binding.tvSummaryTitle.setText(typedArray.getString(R.styleable.SummaryExpensesView_defaultTitle));
        }
        typedArray.recycle();
    }

    public void setStrikethroughPrice(@Nullable String price, @Nullable String promotionCode, @Nullable String promoTag) {
        if (price != null) {
            this.binding.tvSummaryPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.teal_dark));
            this.binding.tvSummaryPriceStrikethrough.setText(price);
            binding.tvSummaryPriceStrikethrough.setPaintFlags(
                    binding.tvSummaryPriceStrikethrough.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            this.binding.tvSummaryPriceStrikethrough.setVisibility(VISIBLE);
            if (promotionCode != null) {
                this.binding.promoLozenge.setText(promoTag);
                this.binding.llPromoTab.setVisibility(VISIBLE);
            }
        }
    }

    public void setPrice(@NonNull String price) {
        binding.tvSummaryPrice.setText(price);
    }

    public void setTitle(@NonNull String title) {
        binding.tvSummaryTitle.setText(title);
    }

    public void setDescription(@NonNull String description) {
        binding.tvSummaryDescription.setText(description);
        binding.tvSummaryDescription.setVisibility(VISIBLE);
    }

}