package com.whitbread.premierinn.hoteldetails.viewholder;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.span.CustomTypefaceSpan;
import com.whitbread.premierinn.common.utils.Truss;
import com.whitbread.premierinn.criteria.roomselector.Room;
import com.whitbread.premierinn.databinding.ViewRoomSubstitutionInfoBinding;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.RoomSubstitutionInfo;
import com.whitbread.premierinn.hoteldetails.uimodel.RoomSubstitutionUiModel;

import java.util.List;

import static com.whitbread.premierinn.common.utils.StringUtils.SPACE;

class RoomSubstitutionViewHolder extends BaseRecyclerViewHolder<RoomSubstitutionUiModel> {

    Typeface boldTypeface;
    Typeface regularTypeface;
    Context context;
    private final ViewRoomSubstitutionInfoBinding binding;

    RoomSubstitutionViewHolder(ViewRoomSubstitutionInfoBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        this.context = binding.getRoot().getContext();

        boldTypeface = ResourcesCompat.getFont(context, R.font.proxima_nova_semibold);
        regularTypeface = ResourcesCompat.getFont(context, R.font.proxima_nova_regular);
    }

    @Override
    public void bind(RoomSubstitutionUiModel model) {
        List<RoomSubstitutionInfo> roomSubstitutionInfoList = model.roomSubstitutionInfoList();
        if (roomSubstitutionInfoList.size() == 1) {
            showRoomSingleSubstitutionMessage(roomSubstitutionInfoList.get(0));
        } else if (roomSubstitutionInfoList.size() > 1) {
            showMultipleRoomSubstitutionMessage(roomSubstitutionInfoList);
        }
    }

    private void showRoomSingleSubstitutionMessage(@NonNull RoomSubstitutionInfo info) {
        Truss dynamicSpannableText = new Truss();
        dynamicSpannableText
                .pushSpan(new CustomTypefaceSpan("", boldTypeface))
                .append(context.getString(R.string.hotel_details_room_substitution_main))
                .popSpan()
                .pushSpan(new CustomTypefaceSpan("", regularTypeface))
                .append("\n")
                .append(context.getString(R.string.hotel_details_room_substitution_single_first))
                .popSpan()
                .append(SPACE)
                .pushSpan(new CustomTypefaceSpan("", boldTypeface))
                .append(info.getFromType().name().toLowerCase())
                .append(SPACE)
                .append(context.getString(R.string.hotel_details_room_substitution_rooms))
                .popSpan()
                .append(SPACE)
                .pushSpan(new CustomTypefaceSpan("", regularTypeface))
                .append(context.getString(R.string.hotel_details_room_substitution_single_middle))
                .popSpan()
                .append(SPACE)
                .pushSpan(new CustomTypefaceSpan("", boldTypeface))
                .append(info.getToType().name().toLowerCase())
                .append(SPACE)
                .append(context.getString(R.string.hotel_details_room_substitution_room))
                .append(".")
                .popSpan();

        binding.roomSubstitutionInfo.setVisibility(View.VISIBLE);
        binding.roomSubstitutionInfo.setText(dynamicSpannableText.build());
    }

    public void showMultipleRoomSubstitutionMessage(@NonNull List<RoomSubstitutionInfo> info) {
        Truss dynamicSpannableText = new Truss();
        dynamicSpannableText
                .pushSpan(new CustomTypefaceSpan("", boldTypeface))
                .append(context.getString(R.string.hotel_details_room_substitution_main))
                .popSpan();

        for (RoomSubstitutionInfo item : info) {
            dynamicSpannableText
                    .append("\n")
                    .pushSpan(new CustomTypefaceSpan("", boldTypeface))
                    .pushSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.teal)))
                    .append(Room.getRoomNameLabel(context, item.getRoomNumberAffected()))
                    .append(SPACE)
                    .popSpan()
                    .popSpan()
                    .pushSpan(new CustomTypefaceSpan("", regularTypeface))
                    .append(context.getString(R.string.hotel_details_room_substitution_changed_from))
                    .append(SPACE)
                    .popSpan()
                    .pushSpan(new CustomTypefaceSpan("", boldTypeface))
                    .append(Room.getRoomTypeLabelLowerCase(context, item.getFromType()))
                    .popSpan()
                    .append(SPACE)
                    .pushSpan(new CustomTypefaceSpan("", regularTypeface))
                    .append(context.getString(R.string.hotel_details_room_substitution_changed_to))
                    .append(SPACE)
                    .popSpan()
                    .pushSpan(new CustomTypefaceSpan("", boldTypeface))
                    .append(Room.getRoomTypeLabelLowerCase(context, item.getToType()))
                    .popSpan()
                    .append("\n");
        }
        binding.roomSubstitutionInfo.setVisibility(View.VISIBLE);
        binding.roomSubstitutionInfo.setText(dynamicSpannableText.build());
    }
}