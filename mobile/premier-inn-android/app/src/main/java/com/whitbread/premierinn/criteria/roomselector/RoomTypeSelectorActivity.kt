package com.whitbread.premierinn.criteria.roomselector

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type
import com.whitbread.premierinn.common.view.roomtype.RoomTypeView
import com.whitbread.premierinn.databinding.ActivityRoomTypesBinding
import com.whitbread.premierinn.domain.common.RoomType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoomTypeSelectorActivity : BaseActivity<ActivityRoomTypesBinding>() {

    private var roomPosition: Int = 0
    private lateinit var selectedOption: RoomType
    private lateinit var allowedOptions: Set<RoomType>

    override fun inflateBinding(inflater: LayoutInflater): ActivityRoomTypesBinding {
        return ActivityRoomTypesBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar(resources.getString(R.string.choose_room_type_title), true)
        analytics.track(ScreenState.ROOM_DIALOG, Type.LOOK_TO_BOOK)

        roomPosition = intent.getIntExtra(ROOM_POSITION_REQUEST, 0)
        selectedOption = intent.getSerializableExtra(EXTRA_PRESELECTED_OPTION) as RoomType
        allowedOptions = intent.getIntArrayExtra(ALLOWED_OPTIONS)!!.map { RoomType.values()[it] }.toSet()

        setupOptions()
        selectOption(selectedOption)
    }

    private fun setupOptions() {
        RoomType.values()
                .filter { roomType -> roomType != RoomType.UNKNOWN }
                .forEach { setupOption(it, allowedOptions.contains(it)) }
    }

    private fun setupOption(roomType: RoomType, isEnabled: Boolean) {
        val roomOptionView = roomOptionView(roomType)
        if (isEnabled) {
            roomOptionView.isEnabled = true
            roomOptionView.setOptionTextColor(R.color.grey_dark)
            roomOptionView.setOptionIconColor(R.color.premier_inn_purple_light)
            roomOptionView.setOptionBackgroundDrawable(R.drawable.accessible_room_option_bg)
            roomOptionView.setOnClickListener {
                selectOption(roomType)
                sendSelectedRoomType()
            }
        } else {
            roomOptionView.visibility = View.GONE
        }
    }

    private fun selectOption(newSelection: RoomType) {
        val selectedOptionView = if (selectedOption != RoomType.UNKNOWN) roomOptionView(selectedOption) else null
        val newSelectedOptionView = roomOptionView(newSelection)

        selectedOptionView?.setOptionBackgroundDrawable(R.drawable.accessible_room_option_bg)
        newSelectedOptionView.setOptionBackgroundDrawable(R.drawable.accessible_room_option_selected_bg)

        selectedOption = newSelection
    }

    private fun sendSelectedRoomType() {
        val intent = Intent().apply {
            putExtra(EXTRA_SELECTED_ROOM_TYPE, selectedOption)
            putExtra(ROOM_POSITION_RESPONSE, roomPosition)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun roomOptionView(roomType: RoomType): RoomTypeView {
        return when (roomType) {
            RoomType.DOUBLE -> binding.optionDouble
            RoomType.SINGLE -> binding.optionSingle
            RoomType.TWIN -> binding.optionTwin
            RoomType.FAMILY -> binding.optionFamily
            RoomType.ACCESSIBLE -> binding.optionAccessible
            RoomType.UNKNOWN -> throw IllegalArgumentException("Unknown room type not supported")
        }
    }

    companion object {

        const val EXTRA_SELECTED_ROOM_TYPE = "selected_room_type_response"
        private const val ROOM_POSITION_RESPONSE = "room_position_response"

        private const val ROOM_POSITION_REQUEST = "room_position_request"
        const val EXTRA_PRESELECTED_OPTION = "preselected_option"
        private const val ALLOWED_OPTIONS = "allowed_options"

        @JvmStatic
        fun createIntent(activity: Activity,
                         roomPosition: Int = 0,
                         allowedOptions: Set<RoomType>,
                         selectedOption: RoomType): Intent {
            return Intent(activity, RoomTypeSelectorActivity::class.java).apply {
                putExtra(ROOM_POSITION_REQUEST, roomPosition)
                putExtra(EXTRA_PRESELECTED_OPTION, selectedOption)
                putExtra(ALLOWED_OPTIONS, allowedOptions.map { it.ordinal }.toIntArray())
            }
        }
    }
}
