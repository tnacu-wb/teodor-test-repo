package com.whitbread.premierinn.domain.common.usecase

import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import javax.inject.Inject

class IsFeatureOn @Inject constructor(private val repository: ContentManagedResourceRepository) {

    operator fun invoke(key: ContentManagedResourceRepository.Key): Boolean {
        return repository.getBoolean(key.value)
    }
}