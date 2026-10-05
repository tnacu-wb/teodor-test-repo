package com.whitbread.premierinn.summary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitbread.premierinn.R
import com.whitbread.premierinn.databinding.BottomSheetMenuAllergyBinding
import com.whitbread.premierinn.summary.models.ParcelableMenuAndAllergyInfo
import com.whitbread.premierinn.summary.adapters.MenuAndAllergyInfoBottomSheetAdapter

class MenuAndAllergyInfoBottomSheet(private val mealsList: List<ParcelableMenuAndAllergyInfo>) :
    BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetMenuAllergyBinding

    private val menuAndAllergyInfoBottomSheetAdapter: MenuAndAllergyInfoBottomSheetAdapter by lazy {
        MenuAndAllergyInfoBottomSheetAdapter(this) }

    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetMenuAllergyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvMenus.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            adapter = menuAndAllergyInfoBottomSheetAdapter
            isNestedScrollingEnabled = false
        }
        menuAndAllergyInfoBottomSheetAdapter.submitList(mealsList)
    }

    fun show(manager: FragmentManager) {
        manager.commit(allowStateLoss = true) {
            add(
                this@MenuAndAllergyInfoBottomSheet,
                MenuAndAllergyInfoBottomSheet::class.java.simpleName
            )
        }
    }
}

