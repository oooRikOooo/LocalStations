package com.andriipedosenko.localstations.ui.dialog.infoMarker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriipedosenko.localstations.domain.usecase.UpdateStationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoMarkerViewModel @Inject constructor(
    private val updateStationUseCase: UpdateStationUseCase
): ViewModel() {
    private val _mode = MutableLiveData<InfoMarkerDialogMode>(InfoMarkerDialogMode.Info)
    val mode: LiveData<InfoMarkerDialogMode> = _mode

    private val _isUpdatedSuccessfully = MutableLiveData<Boolean>()
    val isUpdatedSuccessfully: LiveData<Boolean> = _isUpdatedSuccessfully

    private val _isFailed = MutableLiveData<Boolean>()
    val isFailed: LiveData<Boolean> = _isFailed

    fun updateMode(mode: InfoMarkerDialogMode) {
        _mode.postValue(mode)
    }

    fun updateMarker(
        mcc: String,
        mnc: String,
        lac: String,
        psc: String,
        rat: String,
        cellId: Long,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            updateStationUseCase(
                mcc = mcc,
                mnc = mnc,
                lac = lac,
                psc = psc,
                rat = rat,
                cellId = cellId,
                latitude = latitude,
                longitude = longitude
            ).onSuccess {
                _isUpdatedSuccessfully.postValue(true)
            }.onFailure {
                _isFailed.postValue(true)
            }
        }
    }
}