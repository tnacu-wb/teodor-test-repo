package com.whitbread.premierinn.summarybreakdown;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.span.CustomTypefaceSpan;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.databinding.ItemSummaryBreakdownRoomBinding;
import com.whitbread.premierinn.hoteldetails.DailyRateInput;
import com.whitbread.premierinn.summarybreakdown.view.PriceBlockViewContainer;

import java.util.List;

public class SummaryBreakdownRoomAdapter extends RecyclerView.Adapter<SummaryBreakdownRoomAdapter.SummaryBreakdownRoomHolder> {

    private List<SummaryBreakdownRoom> rooms;
    private DeviceLocaleProvider deviceLocaleProvider;

    public SummaryBreakdownRoomAdapter(List<SummaryBreakdownRoom> rooms,
                                       DeviceLocaleProvider deviceLocaleProvider) {
        this.rooms = rooms;
        this.deviceLocaleProvider = deviceLocaleProvider;
    }

    @Override
    public SummaryBreakdownRoomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemSummaryBreakdownRoomBinding binding = ItemSummaryBreakdownRoomBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SummaryBreakdownRoomAdapter.SummaryBreakdownRoomHolder(binding);
    }

    @Override
    public void onBindViewHolder(SummaryBreakdownRoomHolder holder, int position) {
        holder.update(position, rooms.get(position));
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // ViewHolder
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class SummaryBreakdownRoomHolder extends RecyclerView.ViewHolder {

        private ItemSummaryBreakdownRoomBinding binding;

        public SummaryBreakdownRoomHolder(ItemSummaryBreakdownRoomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void update(int position, @NonNull SummaryBreakdownRoom summaryBreakdownRoom) {
            Context context = binding.tvSummaryBreakdownItemRoomsAndDates.getContext();
            String roomDates = context.getString(R.string.summary_breakdown_dates,
                    summaryBreakdownRoom.formattedArrivalDate(), summaryBreakdownRoom.formattedDepartureDate());
            String roomText = context.getString(R.string.summary_breakdown_room_heading_and_dates, position + 1, roomDates);
            Typeface premierInnRegular = ResourcesCompat.getFont(context, R.font.proxima_nova_regular);
            SpannableString spannableRoomAndDates = new SpannableString(roomText);
            spannableRoomAndDates.setSpan(new CustomTypefaceSpan("", premierInnRegular),
                    spannableRoomAndDates.length() - roomDates.length(), spannableRoomAndDates.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.tvSummaryBreakdownItemRoomsAndDates.setText(spannableRoomAndDates);

            String roomDescription = context.getString(summaryBreakdownRoom.getRoomDescriptionStringId());
            if (summaryBreakdownRoom.cot()) {
                roomDescription = context.getString(R.string.summary_breakdown_room_description_with_cot, roomDescription);
            }
            binding.tvSummaryBreakdownItemRoomDescription.setText(roomDescription);

            String formattedAdults = context.getResources().getQuantityString(R.plurals.number_of_adults_capitalised,
                    summaryBreakdownRoom.adults(), summaryBreakdownRoom.adults());
            String formattedChildren = context.getResources().getQuantityString(R.plurals.number_of_children_capitalised,
                    summaryBreakdownRoom.children(), summaryBreakdownRoom.children());
            String formattedAdultsAndChildren = formattedAdults;
            if (summaryBreakdownRoom.children() > 0) {
                formattedAdultsAndChildren = context.getString(R.string.summary_breakdown_formatted_adults_and_children,
                        formattedAdults, formattedChildren);
            }
            binding.tvSummaryBreakdownItemAdultsAndChildren.setText(formattedAdultsAndChildren);

            binding.tvSummaryBreakdownItemTotal.setText(summaryBreakdownRoom.currencyAndPrice());

            if (summaryBreakdownRoom.dailyRates() != null && summaryBreakdownRoom.dailyRates().size() > 0) {
                for (DailyRateInput dailyRate : summaryBreakdownRoom.dailyRates()) {
                    binding.llSummaryBreakdownPriceByNight.addView(
                            new PriceBlockViewContainer(context).setDailyRate(dailyRate, deviceLocaleProvider).getView());
                }
            } else {
                binding.llSummaryBreakdownPriceByNight.setVisibility(View.GONE);
                binding.ivSummaryBreakdownItemTotal.setVisibility(View.GONE);
                binding.rlSummaryBreakdownItemTotal.setClickable(false);
            }

            binding.rlSummaryBreakdownItemTotal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    togglePriceDetail();
                }
            });

        }

        public void togglePriceDetail() {
            if (binding.llSummaryBreakdownPriceByNight.getVisibility() == View.VISIBLE) {
                binding.ivSummaryBreakdownItemTotal.setImageResource(R.drawable.ic_chevron_down);
                binding.llSummaryBreakdownPriceByNight.setVisibility(View.GONE);
            } else {
                binding.ivSummaryBreakdownItemTotal.setImageResource(R.drawable.ic_chevron_up);
                binding.llSummaryBreakdownPriceByNight.setVisibility(View.VISIBLE);
            }
        }
    }
}
