package com.whitbread.premierinn.domain.resource.usecase

import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import io.reactivex.Completable
import javax.inject.Inject

/**
 *
 */
class SyncRemoteResources @Inject constructor(private val repositoryContentManaged: ContentManagedResourceRepository) {

    fun execute(): Completable {
        return repositoryContentManaged.sync()
    }
}