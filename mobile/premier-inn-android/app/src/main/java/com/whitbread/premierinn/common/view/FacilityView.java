package com.whitbread.premierinn.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.availability.Facility;
import com.whitbread.premierinn.databinding.ViewFacilityBinding;

public class FacilityView extends LinearLayout {

    private ViewFacilityBinding binding;

    public FacilityView(Context context) {
        super(context);
        init(context);
    }

    public FacilityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FacilityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public FacilityView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        binding = ViewFacilityBinding.inflate(LayoutInflater.from(context), this);
    }

    public void setFacility(Facility facility) {
        try {
            binding.ivFacilityIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_available));
            binding.tvFacilityDescription.setText(facility.legend());
        } catch (Exception ignored) {
        }
    }
}
