package com.whitbread.premierinn.ciol.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.adapter.TitleSelectionListAdapter
import com.whitbread.premierinn.ciol.viewmodel.PreStaySharedViewModel
import com.whitbread.premierinn.databinding.FragmentBottomSheetTitleSelectionBinding

class TitleSelectionBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetTitleSelectionBinding

    private val sharedViewModel: PreStaySharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetTitleSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parentView = view.parent as View
        parentView.setBackgroundColor(Color.TRANSPARENT)

        val titleList = resources.getStringArray(R.array.titles).toCollection(ArrayList<String>())

        binding.titleList.layoutManager = LinearLayoutManager(requireContext())
        binding.titleList.adapter =
            TitleSelectionListAdapter(titles = titleList, onTitleSelected = ::onTitleSelected)
    }

    private fun onTitleSelected(title: String) {
        sharedViewModel.updateTitle(title)
        dismiss()
    }
}
