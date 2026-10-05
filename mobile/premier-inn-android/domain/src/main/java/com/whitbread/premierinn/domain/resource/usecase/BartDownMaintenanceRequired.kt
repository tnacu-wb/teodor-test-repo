package com.whitbread.premierinn.domain.resource.usecase

import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.BART_DOWN
import io.reactivex.Single
import javax.inject.Inject

class BartDownMaintenanceRequired @Inject constructor(private val syncRemoteResources: SyncRemoteResources,
                                                      private val repositoryContentManaged: ContentManagedResourceRepository) {
    fun execute(): Single<Boolean> {
        return syncRemoteResources.execute()
                .andThen(
                    Single.fromCallable {
                        repositoryContentManaged.getBoolean(BART_DOWN.value)
                    }
                )
    }
}