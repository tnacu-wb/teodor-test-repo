package com.whitbread.premierinn.common.dagger.retrofitConfiguration

import android.content.Context
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.retrofitConfiguration.BaseOkHttpClientBuilder
import com.whitbread.premierinn.common.retrofitConfiguration.ConsumerType
import com.whitbread.premierinn.common.retrofitConfiguration.GraphQLInterceptor
import com.whitbread.premierinn.data.common.AppPackageDetails
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

class StagingOkHttpClientBuilder(
    context: Context, configuration: AppConfiguration,
    resourceRepository: ContentManagedResourceRepository,
    appPackageDetails: AppPackageDetails,
    consumerType: ConsumerType
) : BaseOkHttpClientBuilder() {
    private var builder: OkHttpClient.Builder

    init {
        val cache = Cache(File(context.cacheDir, "responses"), CACHE_SIZE_KB.toLong())
        val originHeader = getOriginHeaderValueFromGqlEnvironment(configuration)
        this.builder = OkHttpClient().newBuilder()
            .readTimeout(NETWORK_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .connectTimeout(NETWORK_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .writeTimeout(NETWORK_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .authenticator(createAuthenticator())
            .addInterceptor(createLoggingInterceptor())
            .addInterceptor(switchToApolloPathIfApplicable(configuration.isApolloEnabled))
            .addInterceptor(GraphQLInterceptor(consumerType))
            .addInterceptor(createAppInfoGlobalHeaderInterceptor(appPackageDetails, originHeader))
            .addNetworkInterceptor(noCachingOfErrors())

        if (configuration.isCacheEnabled) {
            this.builder.cache(cache).addInterceptor(createOverrideHttpCacheInterceptor())
        }
    }

    private fun getOriginHeaderValueFromGqlEnvironment(configuration: AppConfiguration): String {
        when (configuration.graphQLUrl) {
            Urls.GRAPHQL_DIT -> return Urls.GRAPHQL_DIT.replace("api", "www")
            Urls.GRAPHQL_SIT -> return Urls.GRAPHQL_SIT.replace("api", "www")
            Urls.GRAPHQL_DEMO -> return Urls.GRAPHQL_DEMO.replace("api", "www")
            Urls.GRAPHQL_PRE_PROD -> return Urls.GRAPHQL_PRE_PROD.replace("api", "www")
            Urls.GRAPHQL_PERF -> return Urls.GRAPHQL_PERF.replace("api", "www")
            Urls.GRAPHQL_UAT -> return Urls.GRAPHQL_UAT.replace("api", "www")

            Urls.PRELIVE_WANDA_URL, Urls.PRELIVE_HULK_URL, Urls.LIVE_GRAPHQL_URL -> return Urls.LIVE_GRAPHQL_URL.replace(
                "api",
                "www"
            )

            else -> return "https://www.uat.premierinn.digital"
        }
    }

    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }


    private fun switchToApolloPathIfApplicable(isApollo: Boolean): Interceptor {
        return Interceptor { chain: Interceptor.Chain? ->
            val originalReq = chain!!.request()
            val originalUrl = originalReq.url
            val isApolloGraphqlUrl = isApollo && originalUrl.encodedPath == "/graphql"
            if (isApolloGraphqlUrl) {
                val newUrl = originalUrl.newBuilder()
                    .encodedPath("/apollographql")
                    .build()
                val newRequest = originalReq.newBuilder()
                    .url(newUrl)
                    .build()
                return@Interceptor chain.proceed(newRequest)
            } else {
                return@Interceptor chain.proceed(originalReq)
            }
        }
    }

    public override fun build(): OkHttpClient {
        return this.builder.build()
    }
}
