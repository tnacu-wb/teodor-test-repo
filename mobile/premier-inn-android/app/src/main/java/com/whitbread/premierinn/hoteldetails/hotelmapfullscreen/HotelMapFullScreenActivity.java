package com.whitbread.premierinn.hoteldetails.hotelmapfullscreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.databinding.ActivityHotelMapFullScreenBinding;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HotelMapFullScreenActivity extends BaseActivity<ActivityHotelMapFullScreenBinding> {
    private static final String MAP_INFO_KEY = "map_info_key";

    public static Intent createIntent(@NonNull Context context, @NonNull MapStartInfo mapStartInfo) {
        Intent intent = new Intent(context, HotelMapFullScreenActivity.class);
        intent.putExtra(MAP_INFO_KEY, mapStartInfo);
        return intent;
    }

    @NonNull
    @Override
    protected ActivityHotelMapFullScreenBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityHotelMapFullScreenBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.toolbar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(getIntent().getParcelableExtra(MAP_INFO_KEY));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView(@NonNull MapStartInfo mapStartInfo) {
        setToolbar(mapStartInfo.hotelName(), true);

        binding.mvFullScreen.setMapInfo(
                mapStartInfo.hotelCoordinate(),
                mapStartInfo.searchCoordinate(),
                mapStartInfo.searchTerm(),
                mapStartInfo.hotelBrand());
        binding.tvFullMapMilesDistance.setText(String.format(getString(R.string.map_full_screen_miles_distance), mapStartInfo.distance()));
    }
}
