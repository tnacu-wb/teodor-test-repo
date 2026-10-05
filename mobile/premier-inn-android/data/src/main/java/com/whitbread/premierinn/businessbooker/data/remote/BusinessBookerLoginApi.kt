package com.whitbread.premierinn.businessbooker.data.remote

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface BusinessBookerLoginApi {

    // See https://app.swaggerhub.com/apis/whitbread/Login-API
    @POST("/auth/hotels/login")
    fun login(@Body body: LoginApiContract.BusinessBookerLoginBody,
              @Query("business") business : Boolean = true): Single<LoginApiContract.BusinessBookerLoginResponse>

    @POST("/auth/hotels/logout")
    fun logout(@Body body: LoginApiContract.BusinessBookerLogoutBody): Single<LoginApiContract.BusinessBookerLogoutResponse>
}