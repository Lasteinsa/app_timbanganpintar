package com.banksampahteratai.data.api

import com.banksampahteratai.data.model.TransaksiModel
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.POST
import okhttp3.RequestBody
import retrofit2.http.Multipart
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ApiServices {
    @Multipart
    @POST("login/admin")
    fun login(
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody
    ): Call<ResponseLogin>

    @GET("admin/sessioncheck")
    fun sessionCheck(
        @Header("token")
        token: String
    ): Call<ResponseSessionAuth>

    @GET("admin/getnasabah")
    fun getNasabah(
        @Header("token") token: String,
        @Query ("key") username: String?
    ): Call<ResponseSearchUsers>

    @POST("transaksi/setorsampah")
    fun setorSampah(
        @Header("token") token: String,
        @Body data: TransaksiModel
    ): Call<ResponseTransaksi>

    @GET("sampah/getsampah")
    fun getListHargaSampah(
        @Header("token") token: String
    ): Call<ResponseDataSampah>

    @GET("sampah/getkategori")
    fun getKategoriSampah(

    ): Call<ResponseKategoriSampah>
}

class ApiConfig {
    companion object {
        fun getApiService(): ApiServices {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://banksampah.budiluhur.ac.id/bst_web/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiServices::class.java)
        }
    }
}