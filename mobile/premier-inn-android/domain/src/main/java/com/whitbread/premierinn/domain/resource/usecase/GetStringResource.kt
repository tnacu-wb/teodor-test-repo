package com.whitbread.premierinn.domain.resource.usecase

import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import javax.inject.Inject

class GetStringResource @Inject constructor(private val repositoryContentManaged: ContentManagedResourceRepository) {

     operator fun invoke(key: ContentManagedResourceRepository.Key): String {
        return repositoryContentManaged.getString(key.value)
    }
}