package com.madanes.myapp

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AppService {

    @GET("/w/api.php?format=json&action=query&formatversion=2&generator=prefixsearch&prop=pageimages|info&wbptterms=description&inprop=url&pithumbsize=200")
    fun getSearch(
            @Query("GPS_SEARCH") term: String,
            @Query("GPS_OFFSET") skip: Int,
            @Query("PI_LIMIT") pilimit: Int,
            @Query("GPS_LIMIT") take: Int): Call<Void>

    companion object {
        fun create():AppService
        {

            val okHttpClient = OkHttpClient.Builder()
                    .build()

            val retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            return retrofit.create(AppService::class.java)
        }
    }
}