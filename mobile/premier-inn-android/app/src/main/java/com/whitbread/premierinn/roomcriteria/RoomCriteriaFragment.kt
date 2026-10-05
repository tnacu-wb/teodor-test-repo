package com.whitbread.premierinn.roomcriteria

import android.animation.LayoutTransition
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.whitbread.premierinn.R
import com.whitbread.premierinn.amend.amendguestsrooms.CriteriaViewModelProvider
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.common.fragment.BaseFragment
import com.whitbread.premierinn.common.utils.activityViewModel
import com.whitbread.premierinn.criteria.roomselector.RoomTypeSelectorActivity
import com.whitbread.premierinn.criteria.roomselector.RoomTypeSelectorActivity.Companion.EXTRA_SELECTED_ROOM_TYPE
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.roomcriteria.adultsDecrementAllowed
import com.whitbread.premierinn.domain.roomcriteria.adultsIncrementAllowed
import com.whitbread.premierinn.domain.roomcriteria.childrenDecrementAllowed
import com.whitbread.premierinn.domain.roomcriteria.childrenIncrementAllowed
import com.whitbread.premierinn.domain.roomcriteria.compatibleSortedRoomTypes
import com.whitbread.premierinn.domain.roomcriteria.hasInfants
import com.whitbread.premierinn.domain.roomcriteria.infantsDecrementAllowed
import com.whitbread.premierinn.domain.roomcriteria.infantsIncrementAllowed
import com.whitbread.premierinn.roomcriteria.viewmodel.BaseRoomCriteriaViewModel
import com.whitbread.premierinn.roomcriteria.viewmodel.BaseRoomCriteriaViewModel.AccessibleCotConstraint
import com.whitbread.premierinn.roomcriteria.viewmodel.BaseRoomCriteriaViewModel.MaxAdultConstraintEvent
import com.whitbread.premierinn.roomcriteria.viewmodel.BaseRoomCriteriaViewModel.MaxChildrenConstraintEvent
import com.whitbread.premierinn.roomcriteria.viewmodel.BaseRoomCriteriaViewModel.MaxInfantsConstraint
import com.whitbread.premierinn.roomcriteria.viewmodel.RoomCriteriaEvent
import com.whitbread.premierinn.roomcriteria.viewmodel.RoomCriteriaState
import com.whitbread.premierinn.roomcriteria.viewmodel.RoomCriteriaViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class RoomCriteriaFragment : BaseFragment() {

    private val disposable: AutoCompositeDisposable by lazy { AutoCompositeDisposable(lifecycle) }
    private val viewModel: BaseRoomCriteriaViewModel by activityViewModel { (activity as CriteriaViewModelProvider).getCriteriaViewModel() }

    private var roomId by Delegates.notNull<Int>()
    private var onHomepageActivity: Boolean = false
    private var roomDisplayNumber: Int? = null

    private lateinit var removeRoomBtn: View
    private lateinit var adultPlusBtn: ImageButton
    private lateinit var adultsNumber: TextView
    private lateinit var adultsRange: TextView
    private lateinit var adultMinusBtn: ImageButton
    private lateinit var childrenPlusBtn: ImageButton
    private lateinit var childrenMinusBtn: ImageButton
    private lateinit var childrenNumber: TextView
    private lateinit var childrenRange: TextView
    private lateinit var infantsPlusBtn: ImageButton
    private lateinit var infantsMinusBtn: ImageButton
    private lateinit var infantsNumber: TextView
    private lateinit var roomTypeView: TextView
    private lateinit var roomNameView: TextView
    private lateinit var cotGroup: Group
    private lateinit var infantGroup: Group
    private lateinit var cotSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomId = requireNotNull(requireArguments().getInt(ARG_ROOM_ID)) { "Room Id must be present in args" }
        onHomepageActivity = requireNotNull(requireArguments().getBoolean(HOMEPAGE_ACTIVITY))
        roomDisplayNumber = requireArguments().getInt(ARG_ROOM_DISPLAY_NUMBER).takeIf { it > 0 }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_room_criteria, container, false)
        view as ViewGroup
        view.layoutTransition.enableTransitionType(LayoutTransition.CHANGE_APPEARING)
        view.layoutTransition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING)

        removeRoomBtn = view.findViewById(R.id.room_remove)
        adultPlusBtn = view.findViewById(R.id.adults_plus_button)
        adultMinusBtn = view.findViewById(R.id.adults_minus_button)
        adultsNumber = view.findViewById(R.id.adults_number)
        adultsRange = view.findViewById(R.id.adults_subtext)
        childrenPlusBtn = view.findViewById(R.id.children_plus_button)
        childrenMinusBtn = view.findViewById(R.id.children_minus_button)
        childrenNumber = view.findViewById(R.id.children_number)
        childrenRange = view.findViewById(R.id.children_subtext)
        infantsPlusBtn = view.findViewById(R.id.infants_plus_button)
        infantsMinusBtn = view.findViewById(R.id.infants_minus_button)
        infantsNumber = view.findViewById(R.id.infants_number)
        cotGroup = view.findViewById(R.id.cot_group)
        infantGroup = view.findViewById(R.id.infant_group)
        cotSwitch = view.findViewById(R.id.cot_switch)

        roomNameView = view.findViewById(R.id.room_name)
        roomTypeView = view.findViewById(R.id.room_type)

        val initData = requireArguments().getParcelable<ParcelableRoomCriteria>(ROOM_CRITERIA_SELECTION)

        initData?.let { renderViewRoomCriteria(it) }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (viewModel is RoomCriteriaViewModel) {
            removeRoomBtn.setOnClickListener { (viewModel as RoomCriteriaViewModel).onRoomRemoved(roomId) }
        }

        setClickListeners()

        viewModel.events()
                .filter(allowIfEventMatchesFragmentRoomId)
                .subscribe(::renderEvents)
                .addTo(disposable)

        viewModel.states()
                .distinctUntilChanged()
                .subscribe(::renderState)
                .addTo(disposable)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_CHANGE_ROOM_TYPE -> if (resultCode == RESULT_OK) {
                if (data != null && data.hasExtra(EXTRA_SELECTED_ROOM_TYPE)) {
                    val type = data.extras!![EXTRA_SELECTED_ROOM_TYPE] as RoomType
                    viewModel.onRoomTypeChanged(roomId, type)
                }
            }
        }
    }

    private fun setClickListeners() {
        adultPlusBtn.setOnClickListener { viewModel.onAdultsNumberIncreased(roomId) }
        adultMinusBtn.setOnClickListener { viewModel.onAdultsNumberDecreased(roomId) }
        childrenPlusBtn.setOnClickListener { viewModel.onChildrenNumberIncreased(roomId) }
        childrenMinusBtn.setOnClickListener { viewModel.onChildrenNumberDecreased(roomId) }
        infantsPlusBtn.setOnClickListener { viewModel.onInfantsNumberIncreased(roomId) }
        infantsMinusBtn.setOnClickListener { viewModel.onInfantsNumberDecreased(roomId) }
        cotSwitch.setOnClickListener { v ->
            viewModel.onCotEnabled(roomId, (v as SwitchCompat)
                    .isChecked)
        }

        roomTypeView.setOnClickListener {
            val roomCriteria = (roomTypeView.tag as RoomCriteria)
            startActivityForResult(RoomTypeSelectorActivity.createIntent(requireActivity(),
                    allowedOptions = roomCriteria.compatibleSortedRoomTypes().toSet(),
                    selectedOption = roomCriteria.roomType), REQUEST_CODE_CHANGE_ROOM_TYPE)
        }
    }

    private fun renderViewRoomCriteria(data: ParcelableRoomCriteria) {
        adultsNumber.text = "${data.numberOfAdults}"
        childrenNumber.text = "${data.numberOfChildren}"
        infantsNumber.text = "${data.numberOfInfants}"
        cotGroup.isVisible = data.includeCot
    }

    private fun renderState(state: RoomCriteriaState) {

        roomNameView.text = requireContext().getString(R.string.room_number, roomDisplayNumber
                ?: state.getRoomNumber(roomId))
        removeRoomBtn.isVisible = state.hasMoreThanOneRoom() && onHomepageActivity

        updateAdultsViews(state)
        updateChildrenViews(state)
        updateRoomType(state)

        when (onHomepageActivity) {
            true -> updateInfantsViews(state)
            else -> updateInfantsViewsAmend(state)
        }
    }

    private fun renderEvents(event: RoomCriteriaEvent) {
        when (event) {
            is MaxAdultConstraintEvent -> {
                adultsRange.setTextColor(ContextCompat.getColor(requireContext(), R.color.error_red))
            }
            is MaxChildrenConstraintEvent -> {
                childrenRange.setTextColor(ContextCompat.getColor(requireContext(), R.color.error_red))
            }
            is MaxInfantsConstraint -> requireContext().createAlertDialog(event).show()
            is AccessibleCotConstraint -> {
                cotSwitch.isChecked = false
                requireContext().createAlertDialog(event).show()
            }
        }
    }

    private fun updateAdultsViews(state: RoomCriteriaState) {
        adultPlusBtn.isActivated = state.getRoom(roomId).adultsIncrementAllowed()
        adultMinusBtn.isActivated = state.getRoom(roomId).adultsDecrementAllowed()
        adultsNumber.text = "${state.getRoom(roomId).numberOfAdults}"
        adultsRange.setTextColor(ContextCompat.getColor(requireContext(), R.color.abbey))
    }

    private fun updateChildrenViews(state: RoomCriteriaState) {
        childrenPlusBtn.isActivated = state.getRoom(roomId).childrenIncrementAllowed()
        childrenMinusBtn.isActivated = state.getRoom(roomId).childrenDecrementAllowed()
        childrenNumber.text = "${state.getRoom(roomId).numberOfChildren}"
        childrenRange.setTextColor(ContextCompat.getColor(requireContext(), R.color.abbey))
    }

    private fun updateInfantsViews(state: RoomCriteriaState) {
        infantGroup.visibility = if (onHomepageActivity) View.VISIBLE else View.GONE
        infantsPlusBtn.isActivated = state.getRoom(roomId).infantsIncrementAllowed()
        infantsMinusBtn.isActivated = state.getRoom(roomId).infantsDecrementAllowed()
        infantsNumber.text = "${state.getRoom(roomId).numberOfInfants}"
        cotGroup.isVisible = state.getRoom(roomId).hasInfants()
        cotSwitch.isChecked = state.getRoom(roomId).includeCot

        if (state.getRoom(roomId).hasInfants()) {
            infantsNumber.text = "${state.getRoom(roomId).numberOfInfants}"

            if (cotGroup.visibility == View.VISIBLE) {
                cotSwitch.isChecked = true
            }
        }
    }

    private fun updateInfantsViewsAmend(state: RoomCriteriaState) {
        infantGroup.visibility = if (onHomepageActivity) View.VISIBLE else View.GONE
        cotSwitch.isChecked = state.getRoom(roomId).includeCot
        cotGroup.isVisible = true
    }

    private fun updateRoomType(state: RoomCriteriaState) {
        val roomType = state.getRoom(roomId).roomType
        roomTypeView.text = roomType.getStringResourceName(requireContext())
        roomTypeView.tag = state.getRoom(roomId)
    }

    private val allowIfEventMatchesFragmentRoomId = { event: RoomCriteriaEvent ->
        event.roomId != null && tag == createRoomFragmentTag(event.roomId!!)
    }

    companion object {
        const val REQUEST_CODE_CHANGE_ROOM_TYPE: Int = 989
        const val FRAGMENT_TAG_PREFIX: String = "fragment_room_"
        private const val ARG_ROOM_ID: String = "ARG_ROOM_ID"
        private const val HOMEPAGE_ACTIVITY: String = "HOMEPAGE_ACTIVITY"

        // This is the room number to display. We can't use room_id for amend without large amount of refactoring, it breaks the room number
        private const val ARG_ROOM_DISPLAY_NUMBER: String = "ARG_ROOM_DISPLAY_NUMBER"
        private const val ROOM_CRITERIA_SELECTION: String = "ROOM_CRITERIA_SELECTION"

        @JvmStatic
        fun createRoomFragmentTag(roomId: Int): String = "$FRAGMENT_TAG_PREFIX$roomId"

        @JvmStatic
        fun newInstance(roomId: Int, roomDisplayNumber: Int? = null, homepageActivity: Boolean = false, initRoomCriteria: RoomCriteria? = null) =
                RoomCriteriaFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_ROOM_ID, roomId)
                        putBoolean(HOMEPAGE_ACTIVITY, homepageActivity)
                        roomDisplayNumber?.let {
                            putInt(ARG_ROOM_DISPLAY_NUMBER, it)
                        }
                        initRoomCriteria?.let { roomCriteria ->
                            putParcelable(ROOM_CRITERIA_SELECTION,
                                    ParcelableRoomCriteria(roomCriteria.numberOfAdults,
                                            roomCriteria.numberOfChildren,
                                            roomCriteria.numberOfInfants,
                                            roomCriteria.includeCot,
                                            roomCriteria.roomType,
                                            roomCriteria.roomNumber))
                        }
                    }
                }
    }
}