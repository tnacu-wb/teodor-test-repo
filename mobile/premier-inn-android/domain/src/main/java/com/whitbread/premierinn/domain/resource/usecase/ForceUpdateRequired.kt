package com.whitbread.premierinn.domain.resource.usecase

import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.MINIMUM_SUPPORTED_VERSION
import io.reactivex.Single
import javax.inject.Inject

class ForceUpdateRequired @Inject constructor(private val currentAppVersionCode: Int,
                                              private val syncRemoteResources: SyncRemoteResources,
                                              private val repositoryContentManaged: ContentManagedResourceRepository) {

    fun execute(): Single<Boolean> {
        return syncRemoteResources.execute()
                .andThen(Single.fromCallable {
                    currentAppVersionCode < repositoryContentManaged.getString(MINIMUM_SUPPORTED_VERSION.value).toInt()
                })
    }
}