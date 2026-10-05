package com.whitbread.premierinn.common.retrofitConfiguration

import android.os.Build
import com.whitbread.premierinn.BuildConfig
import com.whitbread.premierinn.data.common.AppPackageDetails
import okhttp3.Authenticator
import okhttp3.Credentials.basic
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.Route


abstract class BaseOkHttpClientBuilder {
    protected abstract fun build(): OkHttpClient

    protected fun createOverrideHttpCacheInterceptor(): Interceptor {
        return Interceptor { chain: Interceptor.Chain? ->
            val request = chain!!.request()
            if (request.header(CACHING_TIME_IN_SECONDS) != null) {
                val offlineRequest = request.newBuilder()
                    .header(
                        "Cache-Control", "public, only-if-cached, max-stale=" + request.header(
                            CACHING_TIME_IN_SECONDS
                        )
                    )
                    .build()
                val offlineResponse = chain.proceed(offlineRequest)
                if (offlineResponse.isSuccessful) {
                    return@Interceptor offlineResponse
                }
            }
            chain.proceed(request)
        }
    }

    protected fun noCachingOfErrors(): Interceptor {
        return Interceptor { chain: Interceptor.Chain? ->
            val response = chain!!.proceed(chain.request())
            if (!response.isSuccessful) {
                return@Interceptor response.newBuilder()
                    .addHeader("Cache-Control", "no-store")
                    .build()
            }
            response
        }
    }

    fun createAppInfoGlobalHeaderInterceptor(
        appPackageDetails: AppPackageDetails,
        originHeader: String?
    ): Interceptor {
        return Interceptor { chain: Interceptor.Chain? ->
            val request = chain!!.request()
            val userAgent = (appPackageDetails.name + " "
                    + BuildConfig.VERSION_NAME + " "
                    + BuildConfig.FLAVOR + " "
                    + "build version code: " + BuildConfig.VERSION_CODE + " "
                    + "Android SDK " + Build.VERSION.SDK_INT)

            val builder = request.newBuilder()
                .addHeader("X-Android-Version", appPackageDetails.versionCode.toString() + "")
                .addHeader("User-Agent", userAgent)

            if (!request.url.toString().contains("graphql")) {
                if (originHeader != null) {
                    builder.addHeader("Origin", originHeader)
                }
            } else {
                builder.addHeader("Accept", "application/json")
                builder.addHeader("apollographql-client-name", "android")
                builder.addHeader("apollographql-client-version", "1.0")
            }
            val newRequest = builder.build()
            chain.proceed(newRequest)
        }
    }

    protected fun createAuthenticator(): Authenticator {
        return Authenticator { route: Route?, response: Response? ->
            if (response!!.request.header("Authorization") != null) {
                return@Authenticator null // Give up, we've already attempted to authenticate.
            }
            response.request.newBuilder()
                .header("Authorization", basic(USERNAME, PASSWORD))
                .build()
        }
    }

    companion object {
        const val USERNAME: String = "what"
        const val PASSWORD: String = "whit"
        const val CACHING_TIME_IN_SECONDS: String = "X-Caching-Time-In-Seconds"

        const val NETWORK_TIMEOUT_SECONDS: Int = 40
        @JvmStatic
        protected val CACHE_SIZE_KB: Int = 20 * 1024 * 1024
        val TWO_MINUTES: String = ":" + 60 * 2
        val SIX_HOURS: String = ":" + 60 * 60 * 6
        val ONE_HOUR: String = ":" + 60 * 60
    }
}
