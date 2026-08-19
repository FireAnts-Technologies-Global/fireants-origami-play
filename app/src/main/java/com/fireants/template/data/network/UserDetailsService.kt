package com.fireants.template.data.network

import com.fireants.template.data.network.model.NetworkUserDetails
import retrofit2.http.GET
import retrofit2.http.Path

interface UserDetailsService {

    @GET("/users/{user}")
    suspend fun getUserDetails(@Path("user") user: String): NetworkUserDetails
}