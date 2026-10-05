package com.whitbread.premierinn.hoteldetails.hotelimagesfullscreen;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.view.gallery.Gallery;
import com.whitbread.premierinn.common.view.gallery.Image;
import com.whitbread.premierinn.common.view.gallery.infinite.implementations.BadgedInfiniteGallery;
import com.whitbread.premierinn.common.view.gallery.infinite.implementations.SimpleInfiniteGallery;
import com.whitbread.premierinn.databinding.ActivityFullScreenGalleryBinding;
import java.util.ArrayList;
import java.util.List;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FullScreenGalleryActivity extends BaseActivity<ActivityFullScreenGalleryBinding> {

    private static final String HOTEL_IMAGES_KEY = "hotel_images_key";
    public static final String HOTEL_IMAGES_POSITION = "hotel_images_position";
    public static final String HOTEL_IMAGES_TYPE = "hotel_images_type";

    private Gallery gallery;

    public static Intent createSimpleGalleryIntent(
            @NonNull Context activity,
            @NonNull ArrayList<String> hotelImages,
            int viewPagerPosition) {
        Intent intent = new Intent(activity, FullScreenGalleryActivity.class);
        intent.putStringArrayListExtra(HOTEL_IMAGES_KEY, hotelImages);
        intent.putExtra(HOTEL_IMAGES_POSITION, viewPagerPosition);
        intent.putExtra(HOTEL_IMAGES_TYPE, String.class);
        return intent;
    }

    public static Intent createBadgedGalleryIntent(
            @NonNull Context activity,
            @NonNull ArrayList<Image> hotelImages,
            int viewPagerPosition) {
        Intent intent = new Intent(activity, FullScreenGalleryActivity.class);
        intent.putParcelableArrayListExtra(HOTEL_IMAGES_KEY, hotelImages);
        intent.putExtra(HOTEL_IMAGES_POSITION, viewPagerPosition);
        intent.putExtra(HOTEL_IMAGES_TYPE, Image.class);
        return intent;
    }

    public static Bundle createAnimationBundle(@NonNull Activity activity, @NonNull View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view,
                activity.getString(R.string.full_screen_gallery_transition));
        return options.toBundle();
    }

    @NonNull
    @Override
    protected ActivityFullScreenGalleryBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityFullScreenGalleryBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Class type = (Class) getIntent().getSerializableExtra(HOTEL_IMAGES_TYPE);
        int position = getIntent().getIntExtra(HOTEL_IMAGES_POSITION, -1);
        if (type == String.class) {
            List<String> images = getIntent().getStringArrayListExtra(HOTEL_IMAGES_KEY);
            setUpSimpleInfiniteGallery(images, position);
        } else if (type == Image.class) {
            List<Image> images = getIntent().getParcelableArrayListExtra(HOTEL_IMAGES_KEY);
            setUpBadgedInfiniteGallery(images, position);
        }
        binding.ivHotelImagesFullScreenBackArrow.setOnClickListener(__ -> onBackPressed());

        analytics.track(ScreenState.HOTEL_IMAGES_FULL_SCREEN, Type.LOOK_TO_BOOK);
    }

    private void setUpSimpleInfiniteGallery(@NonNull List<String> images, int position) {
        SimpleInfiniteGallery simpleGallery = (SimpleInfiniteGallery) binding.simpleGalleryStub.inflate();
        simpleGallery.images(images);
        int selectedPosition = position >= 0 ? position : simpleGallery.firstRealPosition();
        simpleGallery.setCurrentItem(selectedPosition);
        gallery = simpleGallery;
    }

    private void setUpBadgedInfiniteGallery(@NonNull List<Image> images, int position) {
        BadgedInfiniteGallery badgedGallery = (BadgedInfiniteGallery) binding.badgedGalleryStub.inflate();
        badgedGallery.images(images);
        int selectedPosition = position >= 0 ? position : badgedGallery.firstRealPosition();
        badgedGallery.setCurrentItem(selectedPosition);
        gallery = badgedGallery;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(HOTEL_IMAGES_POSITION, gallery.currentItem()); // is this used at all?
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();
    }
}
