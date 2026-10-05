package com.whitbread.premierinn.common.view;

import static com.google.android.flexbox.FlexWrap.WRAP;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.google.android.flexbox.FlexboxLayout;
import com.whitbread.premierinn.api.Urls;
import com.whitbread.premierinn.api.response.AcceptedCreditCard;
import java.util.List;

public class CreditCardImagesView extends FlexboxLayout {
    private static final int CREDIT_CARD_IMAGE_WIDTH = 34;
    private static final int CREDIT_CARD_IMAGE_HEIGHT = 22;
    private static final int CREDIT_CARD_MARGIN_RIGHT = 8;
    private static final int CREDIT_CARD_MARGIN_BOTTOM = 8;

    public CreditCardImagesView(Context context) {
        super(context);
        init();
    }

    public CreditCardImagesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CreditCardImagesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFlexWrap(WRAP);
    }

    public void displayCreditCards(@NonNull List<AcceptedCreditCard> acceptedCreditCards) {
        final float density = getContext().getResources().getDisplayMetrics().density;
        for (AcceptedCreditCard acceptedCreditCard : acceptedCreditCards) {
            NetworkImageView imageView = new NetworkImageView(getContext());
            FlexboxLayout.LayoutParams layoutParams =
                    new FlexboxLayout.LayoutParams((int) (CREDIT_CARD_IMAGE_WIDTH * density),
                            (int) (CREDIT_CARD_IMAGE_HEIGHT * density));
            layoutParams.setMargins(0, 0, (int) (CREDIT_CARD_MARGIN_RIGHT * density),
                    (int) (CREDIT_CARD_MARGIN_BOTTOM * density));
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            addView(imageView);
            final String creditCardCode = Urls.CONTENT_BASE_URL.concat(acceptedCreditCard.schemeLogo());
            imageView.load(creditCardCode);
        }
    }
}
