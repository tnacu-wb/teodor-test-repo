package com.whitbread.premierinn.roomcriteria

import android.animation.LayoutTransition
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.whitbread.premierinn.R
import com.whitbread.premierinn.amend.amendguestsrooms.CriteriaViewModelProvider
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.databinding.ActivityRoomCriteriaBinding
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.roomcriteria.viewmodel.BaseRoomCriteriaViewModel
import com.whitbread.premierinn.roomcriteria.viewmodel.RoomCriteriaEvent
import com.whitbread.premierinn.roomcriteria.viewmodel.RoomCriteriaState
import com.whitbread.premierinn.roomcriteria.viewmodel.RoomCriteriaViewModel
import com.whitbread.premierinn.roomcriteria.viewmodel.RoomCriteriaViewModel.MaxRoomConstraintEvent
import com.whitbread.premierinn.roomcriteria.viewmodel.SendSelectionEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoomCriteriaActivity : BaseActivity<ActivityRoomCriteriaBinding>(), CriteriaViewModelProvider {

    private val disposable: AutoCompositeDisposable by lazy { AutoCompositeDisposable(lifecycle) }
    private val viewModel: RoomCriteriaViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater): ActivityRoomCriteriaBinding {
        return ActivityRoomCriteriaBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbar(getString(R.string.criteria_toolbar_text), true)
        viewModel.onSubmitSelection()
        binding.criteriaDone.setOnClickListener { viewModel.onSubmitSelection() }
        binding.fragmentContainer.layoutTransition.enableTransitionType(LayoutTransition.CHANGE_APPEARING)
        binding.fragmentContainer.layoutTransition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING)

        viewModel.states()
                .distinctUntilChanged(allowWhenNumberOfRoomsChanges)
                .map { it.getRoomIds() }
                .subscribe(::renderRoomFragmentUpdates)
                .addTo(disposable)

        viewModel.states()
                .distinctUntilChanged()
                .subscribe(::renderNumberOfGuestsButtonUpdates)
                .addTo(disposable)

        viewModel.events()
                .subscribe(::renderEvents)
                .addTo(disposable)

        if (viewModel.isBusinessUser()) {
            binding.criteriaAddRoom.isVisible = false
            binding.innBusinessInfoBox.isVisible = true
        } else{
            binding.criteriaAddRoom.isVisible = true
            binding.criteriaAddRoom.setOnClickListener { viewModel.onRoomAdded() }
        }
    }

    override fun getCriteriaViewModel(): BaseRoomCriteriaViewModel {
        return viewModel
    }

    private fun renderEvents(event: RoomCriteriaEvent) {
        when (event) {
            is MaxRoomConstraintEvent -> createAlertDialog(event.message, event.phoneNumber,event.isGroupFormRequired).show()
            is SendSelectionEvent -> sendData(event.selection)
        }
    }

    private fun renderRoomFragmentUpdates(newRoomTagList: List<Int>) {
        val oldRoomIdsList = supportFragmentManager.fragments.asIterable()
                .mapNotNull { it.tag?.removePrefix(RoomCriteriaFragment.FRAGMENT_TAG_PREFIX)?.toInt() }
                .toList()
        val roomsToDelete = oldRoomIdsList.subtract(newRoomTagList).toList()

        newRoomTagList.forEach {
            addRoomIfNeeded(it)
        }

        roomsToDelete.forEach {
            removeRoomIfExist(it)
        }
    }

    private fun addRoomIfNeeded(roomId: Int) {
        val fragmentTag = RoomCriteriaFragment.createRoomFragmentTag(roomId)
        if (supportFragmentManager.findFragmentByTag(fragmentTag) == null) {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.fragment_container, RoomCriteriaFragment.newInstance(roomId = roomId, homepageActivity = true), fragmentTag)
                commitNow()
            }
        }
    }

    private fun removeRoomIfExist(roomId: Int) {
        val fragmentTag = RoomCriteriaFragment.createRoomFragmentTag(roomId)
        val fragmentToRemove = supportFragmentManager.findFragmentByTag(fragmentTag)
        if (fragmentToRemove != null) {
            supportFragmentManager.beginTransaction().apply {
                remove(fragmentToRemove)
                commitNow()
            }
        }
    }

    private fun renderNumberOfGuestsButtonUpdates(state: RoomCriteriaState) {
        binding.criteriaDoneSubtext.text = state.formatTotalGuests(this)
    }

    private fun sendData(selection: RoomCriteriaState) {
        val intent = Intent().apply {
            putExtra(ROOM_CRITERIA_SELECTION, selection.toParcelableRooms())
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    private val allowWhenNumberOfRoomsChanges = { old: RoomCriteriaState, new: RoomCriteriaState -> old.getNumberOfRooms() == new.getNumberOfRooms() }

    companion object {

        const val ROOM_CRITERIA_SELECTION = "room_criteria_selection"

        @JvmStatic
        fun createIntent(context: Context, initRoomCriteria: List<RoomCriteria>? = null): Intent {
            return Intent(context, RoomCriteriaActivity::class.java).apply {
                initRoomCriteria?.let { list ->
                    putExtra(ROOM_CRITERIA_SELECTION, ParcelableRooms(list.map { it.toParcelable() }))
                }
            }
        }
    }
}