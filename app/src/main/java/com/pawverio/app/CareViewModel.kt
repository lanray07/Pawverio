package com.pawverio.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pawverio.core.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class CareViewModel(app: Application) : AndroidViewModel(app) {
    val repository = CareRepository(PawverioDatabase.open(app))
    val pets =
        repository
            .observePets()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val tasks =
        repository
            .observeTasks()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val events =
        repository
            .observeEvents()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val health =
        repository
            .observeHealthRecords()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val weights =
        repository
            .observeWeights()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val members =
        repository
            .observeHousehold()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
