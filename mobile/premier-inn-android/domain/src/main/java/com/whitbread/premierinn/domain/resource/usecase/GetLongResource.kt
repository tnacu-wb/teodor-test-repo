package com.whitbread.premierinn.domain.resource.usecase

import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import javax.inject.Inject

class GetLongResource @Inject constructor(private val repositoryContentManaged: ContentManagedResourceRepository) {

     operator fun invoke(key: ContentManagedResourceRepository.Key): Long {
        return repositoryContentManaged.getLong(key.value)
    }
}