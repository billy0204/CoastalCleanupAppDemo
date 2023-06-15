package com.example.usjprojectdemo.Data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataViewModel :ViewModel(){

    private val _activityItemLiveData = MutableLiveData<List<ActivityItem>>()
    val activityItemLiveData :LiveData<List<ActivityItem>> = _activityItemLiveData

    private val  repository = ActivityRepository()

    fun fetchActivity(){
        repository.fetchActivity(_activityItemLiveData)
    }
}