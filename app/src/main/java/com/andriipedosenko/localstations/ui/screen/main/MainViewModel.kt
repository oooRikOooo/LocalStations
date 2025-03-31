package com.andriipedosenko.localstations.ui.screen.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriipedosenko.localstations.domain.model.Station
import com.andriipedosenko.localstations.domain.usecase.DeleteStationUseCase
import com.andriipedosenko.localstations.domain.usecase.GetStationInRangeUseCase
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getStationInRangeUseCase: GetStationInRangeUseCase,
    private val deleteStationUseCase: DeleteStationUseCase
) : ViewModel() {

    private val _currentClusterStation = MutableLiveData<Station?>()
    val currentClusterStation: LiveData<Station?> = _currentClusterStation

    private val _stations = MutableLiveData<List<Station>>()
    val stations: LiveData<List<Station>> = _stations

    fun setCurrentClusterStation(station: Station?) {
        _currentClusterStation.postValue(station)
    }

    fun getVisibleStations(
        visibleCoordinates: LatLngBounds,
        zoomLevel: Float
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val limit: Int = when {
                zoomLevel > 15 -> 500
                zoomLevel > 12 -> 1000
                zoomLevel > 10 -> 2000
                zoomLevel > 7 -> 5000
                else -> 7000
            }

            getStationInRangeUseCase(
                visibleCoordinates = visibleCoordinates,
                limit = limit
            ).onSuccess {
                _stations.postValue(it)
            }
        }
    }

    fun deleteStation() {
        viewModelScope.launch(Dispatchers.IO) {
            val station = _currentClusterStation.value ?: return@launch

            deleteStationUseCase(station.cellId)
                .onSuccess {
                    _currentClusterStation.postValue(null)
                }
        }
    }
}