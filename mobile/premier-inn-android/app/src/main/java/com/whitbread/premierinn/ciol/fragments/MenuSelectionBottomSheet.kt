package com.whitbread.premierinn.ciol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.entity.upsells.MenuUiModel
import com.whitbread.premierinn.ciol.views.upsells.MenuSelectionEntryView
import com.whitbread.premierinn.databinding.ViewMenuSelectionBottomSheetBinding

const val MENU_SELECTION_EXTRA_MENUS_KEY = "MENU_SELECTION_EXTRA_MENUS_KEY"

class MenuSelectionBottomSheet: BottomSheetDialogFragment() {

    private lateinit var binding: ViewMenuSelectionBottomSheetBinding
    private var onMenuOpened: ((errorWhenOpening: Boolean) -> Unit)? = null

    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewMenuSelectionBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.selectionMenuCloseImageView.setOnClickListener {
            dismiss()
        }

        arguments?.getParcelableArrayList<MenuUiModel>(MENU_SELECTION_EXTRA_MENUS_KEY)?.let { menus ->
            binding.menusContainerLayout.removeAllViews()
            menus.forEachIndexed { index, menuUiModel ->
                binding.menusContainerLayout.addView(
                    MenuSelectionEntryView(requireContext()).apply {
                        bind(
                            activity = requireActivity(),
                            menu = menuUiModel,
                            isLastIndex = menus.lastIndex == index
                        ) { errorWhenOpening ->
                            this@MenuSelectionBottomSheet.dismiss()
                            onMenuOpened?.invoke(errorWhenOpening)
                        }
                    }
                )
            }
        }
    }

    fun show(manager: FragmentManager, onMenuOpened: (errorWhenOpening: Boolean) -> Unit) {
        manager.commit(allowStateLoss = true) {
            add(this@MenuSelectionBottomSheet, MenuSelectionBottomSheet::class.java.simpleName)
            this@MenuSelectionBottomSheet.onMenuOpened = onMenuOpened
        }
    }
}