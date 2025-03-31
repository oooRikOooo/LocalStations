package com.andriipedosenko.localstations.ui.dialog.createMarker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriipedosenko.localstations.domain.usecase.CreateStationUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateMarkerViewModel @Inject constructor(
    private val createStationUseCase: CreateStationUseCase
) : ViewModel() {

    private val _isCreatedSuccessfully = MutableLiveData<Boolean>()
    val isCreatedSuccessfully: LiveData<Boolean> = _isCreatedSuccessfully

    private val _isFailed = MutableLiveData<Boolean>()
    val isFailed: LiveData<Boolean> = _isFailed

    fun createMarker(
        mcc: String,
        mnc: String,
        lac: String,
        psc: String,
        rat: String,
        latLng: LatLng
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            createStationUseCase(
                mcc = mcc,
                mnc = mnc,
                lac = lac,
                psc = psc,
                rat = rat,
                latitude = latLng.latitude,
                longitude = latLng.longitude
            ).onSuccess {
                _isCreatedSuccessfully.postValue(true)
            }.onFailure {
                _isFailed.postValue(true)
            }
        }
    }
}