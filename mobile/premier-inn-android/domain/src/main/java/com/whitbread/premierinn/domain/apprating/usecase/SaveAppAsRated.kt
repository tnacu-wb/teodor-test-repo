package com.whitbread.premierinn.domain.apprating.usecase

import com.whitbread.premierinn.domain.apprating.repository.AppRatingRepository
import javax.inject.Inject

class SaveAppAsRated @Inject constructor(private val repository: AppRatingRepository) {
    fun execute() {
        return repository.setAsAppRated(true)
    }
}