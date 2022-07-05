package com.ezz.geoip.ui

import com.ezz.geoip.api.ApiService
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getInfo(ip: String) = apiService.getInfo(ip)
}
