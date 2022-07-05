package com.ezz.geoip.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ezz.geoip.models.Info
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    private val infoMLD = MutableLiveData<Info>()
    val infoLD: LiveData<Info> = infoMLD
    var job: Job? = null

    val loading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun getIPInfo(ip: String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = mainRepository.getInfo(ip)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    infoMLD.postValue(response.body())
                    loading.value = false
                    Log.d("TAG", "success: $response")
                } else {
                    onError("Error : ${response.message()} ")
                    Log.d("TAG", "error: $response")
                }
            }
        }
    }
    private fun onError(message: String) {
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
