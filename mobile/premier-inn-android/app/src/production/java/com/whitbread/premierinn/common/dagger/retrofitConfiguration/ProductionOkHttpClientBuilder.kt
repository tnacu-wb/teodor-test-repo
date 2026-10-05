package com.whitbread.premierinn.common.dagger.retrofitConfiguration

import android.content.Context
import com.whitbread.premierinn.common.retrofitConfiguration.BaseOkHttpClientBuilder
import com.whitbread.premierinn.common.retrofitConfiguration.ConsumerType
import com.whitbread.premierinn.common.retrofitConfiguration.GraphQLInterceptor
import com.whitbread.premierinn.data.common.AppPackageDetails
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

class ProductionOkHttpClientBuilder(
    context: Context,
    resourceRepository: ContentManagedResourceRepository,
    appPackageDetails: AppPackageDetails,
    consumerType: ConsumerType
) : BaseOkHttpClientBuilder() {
    private val builder: OkHttpClient.Builder

    init {
        val cache = Cache(File(context.getCacheDir(), "responses"), CACHE_SIZE_KB.toLong())

        this.builder = OkHttpClient().newBuilder()
            .readTimeout(NETWORK_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .connectTimeout(NETWORK_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .writeTimeout(NETWORK_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .authenticator(createAuthenticator())
            .addInterceptor(createAppInfoGlobalHeaderInterceptor(appPackageDetails, null))
            .addInterceptor(GraphQLInterceptor(consumerType))
            .cache(cache)
            .addInterceptor(createOverrideHttpCacheInterceptor())
            .addNetworkInterceptor(noCachingOfErrors())
    }

    public override fun build(): OkHttpClient {
        return builder.build()
    }
}
