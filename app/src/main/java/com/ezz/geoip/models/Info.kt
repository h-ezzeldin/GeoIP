package com.ezz.geoip.models

data class Info(
    val city: String,
    //val continent_code: String,
    val continent_name: String,
    //val country_code: String,
    val country_name: String,
    val ip: String,
    val latitude: Double,
    //val location: Location,
    val longitude: Double,
    //val region_code: String,
    val region_name: String,
    //val type: String,
    val zip: String

)