package com.whitbread.premierinn.businessbooker.data.remote

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface CompanyApi {

    //https://app.swaggerhub.com/apis/whitbread/Company-API/2.3#/
    @Headers("Cache-Control: no-store")
    @GET("/company/{companyId}")
    fun getCompany(@Header("Authorization") bearerToken: String,
                   @Header("X-acf-sensor-data") sensorData: String,
                   @Path("companyId") companyId: String,
                   @Query("country") country : String,
                   @Query("language") language: String
    ): Single<CompanyApiContract.CompanyDetailsResponse>
}