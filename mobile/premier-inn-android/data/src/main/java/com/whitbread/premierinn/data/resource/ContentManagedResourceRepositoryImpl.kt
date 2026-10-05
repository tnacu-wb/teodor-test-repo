package com.whitbread.premierinn.data.resource

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

/**
 *  Note:
 *  An app can fetch a maximum of 5 times in a 60 minute window before the SDK begins to throttle and returns FirebaseRemoteConfigFetchThrottledException.
 *  https://firebase.google.com/docs/remote-config/android#caching_and_throttling
 */
class ContentManagedResourceRepositoryImpl @Inject constructor(private val remoteConfig: FirebaseRemoteConfig,
                                                               private val persistenceManager: SimplePersistenceManager) : ContentManagedResourceRepository {
    override fun getBoolean(key: String) = remoteConfig.getBoolean(key)

    override fun getString(key: String): String = remoteConfig.getString(key)

    override fun getStringSingle(key: String): Single<String> = Single.fromCallable { remoteConfig.getString(key) }

    override fun getLong(key: String): Long = remoteConfig.getLong(key)
    override fun sync(): Completable {

        return Completable.create {
            remoteConfig.fetch(getExpiryCache())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (remoteConfig.activate().isSuccessful) {
                                persistenceManager.setRemoteConfigAsStale(false)
                            }
                            it.onComplete()
                        } else {
                            it.tryOnError(task.exception ?: IllegalStateException("Firebase Unknown Exception"))
                        }
                    }
        }
    }

    private fun getExpiryCache(): Long = if (persistenceManager.isRemoteConfigStale()) 0L else 1800L

}