package com.example.geoquiz.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface ReverseGeocoderAPI {
    @Headers("Content-Type: application/json")
    @GET("V1/reverseGeoCoder")
    suspend fun get(@Query("lat") lat:String, @Query("lon") lon:String,@Query("appid") appid:String, @Query("output") output:String = "json")
    : Response<ReverseGeoCoderData>
}
