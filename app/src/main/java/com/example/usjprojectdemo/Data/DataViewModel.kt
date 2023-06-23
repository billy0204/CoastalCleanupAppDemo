package com.example.usjprojectdemo.Data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataViewModel : ViewModel() {

    private val _activityItemLiveData = MutableLiveData<List<ActivityItem>>()
    private val _joindedData = MutableLiveData<List<UserData>>()

    val activityItemLiveData: LiveData<List<ActivityItem>> = _activityItemLiveData
    val joindedData: LiveData<List<UserData>> = _joindedData

    private val activityRepository = ActivityRepository()
    private val userDataRepository = UserDataRepository()

    fun fetchActivity() {
        activityRepository.fetchActivity(_activityItemLiveData)
    }

    fun fetchJoinedActivity(){
        userDataRepository.fetchJoinedActivities(_joindedData)
    }
    fun fectchUserData(){
        activityRepository.fetchActivity(_activityItemLiveData)
    }

}