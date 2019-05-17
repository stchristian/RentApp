package com.example.rentapp.network

import com.example.rentapp.data.Gadget
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import rx.Observable
import java.util.*

/**
 * A RESTAPI-nk interfésze.
 */
interface GadgetRESTApi{
    companion object {
        var token : String? = null
        private var instance : GadgetRESTApi? = null

        fun getInstance() : GadgetRESTApi {
            if(instance == null) {
                // Add interceptor to add authorization headers to http request
                val okHttpClient = OkHttpClient.Builder()
                        .addInterceptor { chain ->
                            var request = chain.request()
                            request = token?.let {
                                request.newBuilder()
                                        .addHeader("Authorization", token)
                                        .build()
                            } ?: request
                            chain.proceed(request)
                        }.build()

                val retrofit = Retrofit.Builder()
                        .addCallAdapterFactory(
                                RxJavaCallAdapterFactory.create())
                        .addConverterFactory(
                                GsonConverterFactory.create())
                        .baseUrl("http://152.66.180.164:3000/api/")
                        .client(okHttpClient)
                        .build()

                instance = retrofit.create(GadgetRESTApi::class.java)
            }
            return instance!!
        }
    }

    @POST("users")
    @FormUrlEncoded
    fun registerUser(@Field("email") email: String, @Field("jelszo") jelszo: String, @Field("vezeteknev") vezeteknev: String,
                     @Field("keresztnev") keresztnev: String, @Field("telefonszam") telefonszam: String) : Observable<RegistrationResponse>

    @POST("login")
    @FormUrlEncoded
    fun loginUser(@Field("email") email: String, @Field("jelszo") jelszo: String) : Observable <LoginResponse>

    @GET("verify")
    fun  verifyToken(@Header("Authorization") token : String) : Observable<LoginResponse>

//    @GET("users/me")
//    fun getMyUser(@Header("Authorization") token : String) : Observable<UserResponse>

    @GET("users/me")
    fun getMyUser() : Observable<UserResponse>

    @GET("gadgets")
    fun getGadgets() : Observable<List<Gadget>>

}

data class RegistrationResponse(
    val error: List<Error>?,
    val success: Boolean,
    val msg: String
)

data class Error(
    val `param`: String,
    val location: String,
    val msg: String,
    val value: String
)

data class LoginResponse(
    val token: String?,
    val success: Boolean,
    val error: List<Error>?
)

data class UserResponse (
        val success: Boolean,
        val user : User?,
        val msg : String?
)

data class User(
    val __v: Int,
    val _id: String,
    val admin: Boolean,
    val email: String,
    val eszkozfelelos: Boolean,
    val jelszo: String,
    val keresztnev: String,
    val megerositve: Boolean,
    val reg_datum: String,
    val telefonszam: String,
    val vezeteknev: String
)
