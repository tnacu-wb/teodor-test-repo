package com.whitbread.premierinn.common.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.whitbread.premierinn.R;

public class PrivacyPolicyBannerView extends InfoMessageBoxView {

    public PrivacyPolicyBannerView(Context context) {
        super(context);
        init(context);
    }

    public PrivacyPolicyBannerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PrivacyPolicyBannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundResource(R.color.mint_light_fifty_percent);

        binding.icon.setVisibility(VISIBLE);
        binding.icon.setImageResource(R.drawable.ic_security_white_24dp);
        binding.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);

        String label = context.getString(R.string.privacy_policy_banner_label, context.getString(R.string.privacy_policy_web_url));
        setHtmlText(label);

        int verticalMargin = (int) getResources().getDimension(R.dimen.generic_margin_8);
        int horizontalMargin = (int) getResources().getDimension(R.dimen.generic_margin_16);

        setPadding(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
    }
}
