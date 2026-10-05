package com.whitbread.premierinn.landing

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.whitbread.premierinn.R
import com.whitbread.premierinn.search.SearchActivity
import com.whitbread.premierinn.searchresults.SearchResultsActivity
import com.whitbread.premierinn.utils.BaseRobot

class LandingRobot : BaseRobot() {

    fun openSearchSuggestions() {
        view(R.id.landing_location_text).perform(click())
        Intents.intended(IntentMatchers.hasComponent(SearchActivity::class.java.name))
    }

    fun searchForHotels() {
        view(R.id.landing_search_button).perform(click())
        Intents.intended(IntentMatchers.hasComponent(SearchResultsActivity::class.java.name))
    }
}