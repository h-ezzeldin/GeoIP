package com.ezz.geoip.api

import com.ezz.geoip.models.Info
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("{ip}?access_key=a12052f0b2ff6f688bf4ddfd6547e9e7")
    suspend fun getInfo(@Path("ip") ip: String): Response<Info>
}
