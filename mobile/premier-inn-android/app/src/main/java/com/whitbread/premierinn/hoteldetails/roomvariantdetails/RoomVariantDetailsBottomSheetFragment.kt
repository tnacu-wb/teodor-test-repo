package com.whitbread.premierinn.hoteldetails.roomvariantdetails

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.whitbread.premierinn.R
import com.whitbread.premierinn.base.view.bottomsheet.FullScreenBottomSheetDialogFragment
import com.whitbread.premierinn.base.view.bottomsheet.NoMidDismissBackNavigationCallback
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.SpaceDividerItemDecoration
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.databinding.FragmentRoomVariantsBinding
import com.whitbread.premierinn.domain.common.toGermanIfApplicable
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoomVariantDetailsBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    private lateinit var binding: FragmentRoomVariantsBinding
    private lateinit var tabs: TabLayout
    lateinit var tabContent: ViewPager
    private val disposable: AutoCompositeDisposable by lazy { AutoCompositeDisposable(lifecycle) }
    private val viewModel: RoomVariantDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoomVariantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeButton.setOnClickListener {
            closeDialog()
        }

        viewModel.states()
            .distinctUntilChanged(contentIsDifferent)
            .subscribe { display(it) }
            .addTo(disposable)

        tabs = binding.roomDetailTabLayout
        tabContent = binding.roomDetailViewPager

    }

    private val contentIsDifferent =
        { s1: RoomVariantDetailsState, s2: RoomVariantDetailsState -> s1.contentList == s2.contentList }

    private fun display(state: RoomVariantDetailsState) {
        state.contentList?.let { list ->
            setupTabContent(list, state.language)
            setupTabs()
            tabs.getTabAt(0)?.select()
        }
    }

    override fun getSheetCallback(bottomSheetDialog: BottomSheetDialog) =
        NoMidDismissBackNavigationCallback(bottomSheetDialog, this)


    private fun setupTabContent(roomVariantsInfoDetails: List<Hotel.RoomVariantDetails>, language: String) {
        tabContent.removeAllViews()
        val listOfGroupedRooms = mutableListOf<List<Hotel.RoomVariantDetails>>()
        roomVariantsInfoDetails.groupBy { it.room }.map {
            listOfGroupedRooms.add(it.value)
        }

        tabContent.adapter = object : PagerAdapter() {
            override fun getCount(): Int {
                return listOfGroupedRooms.size
            }

            override fun isViewFromObject(view: View, any: Any): Boolean {
                return view === any
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {

                val content = LayoutInflater.from(container.context)
                    .inflate(R.layout.room_variant_content, container, false)
                val layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                val snapHelper: SnapHelper = PagerSnapHelper()

                val recyclerView = content.findViewById<RecyclerView>(R.id.room_variant_recycler_view)
                recyclerView.layoutManager = layoutManager
                recyclerView.addItemDecoration(SpaceDividerItemDecoration(16, RecyclerView.HORIZONTAL))
                recyclerView.onFlingListener = null

                snapHelper.attachToRecyclerView(recyclerView)

                recyclerView.adapter = RoomVariantDetailsAdapter(container.context, listOfGroupedRooms[position])

                tabContent.addView(content)
                return content
            }

            override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
                container.removeView(any as View)
            }

            override fun getPageTitle(position: Int): CharSequence {
                return listOfGroupedRooms[position][0].room.toGermanIfApplicable(language)
            }
        }
    }

    private fun setTabTextStyle(tab: TabLayout.Tab, style: Int) {
        val viewGroup = tabs.getChildAt(0) as ViewGroup
        val viewGroupTab = viewGroup.getChildAt(tab.position) as ViewGroup
        val tabChildrenCount = viewGroupTab.childCount;

        for (i in 0..tabChildrenCount) {
            val tabViewChild = viewGroupTab.getChildAt(i);
            if (tabViewChild is TextView) {
                tabViewChild.setTypeface(null, style)
            }
        }
    }

    private fun setupTabs() {
        tabs.setupWithViewPager(tabContent)
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                setTabTextStyle(tab, Typeface.BOLD)
            }


            override fun onTabUnselected(tab: TabLayout.Tab) {
                setTabTextStyle(tab, Typeface.NORMAL)

            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                setTabTextStyle(tab, Typeface.BOLD)
            }
        })
    }

    companion object {
        const val HOTEL_CODE = "hotel_code_key"
        const val HOTEL_BRAND = "hotel_brand_key"
        const val TAG = "RoomVariantDetailsBottomSheetFragment"
    }
}