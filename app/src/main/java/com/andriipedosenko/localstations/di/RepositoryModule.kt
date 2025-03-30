package com.andriipedosenko.localstations.di

import com.andriipedosenko.localstations.data.repository.StationRepositoryImpl
import com.andriipedosenko.localstations.domain.repository.StationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindStationRepository(stationRepositoryImpl: StationRepositoryImpl): StationRepository

}