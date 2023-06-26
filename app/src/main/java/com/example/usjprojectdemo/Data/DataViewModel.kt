package com.example.usjprojectdemo.Data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class DataViewModel : ViewModel() {

    private val _activityItemLiveData = MutableLiveData<List<ActivityItem>>()
    val activityItemLiveData: LiveData<List<ActivityItem>> = _activityItemLiveData


    private val _joinedActivitiesLiveData = MutableLiveData<List<JoinedActivity>>()
    val joinedActivitiesLiveData: LiveData<List<JoinedActivity>> = _joinedActivitiesLiveData


    private val activityRepository = ActivityRepository()
    private val userDataRepository = UserDataRepository()

    fun fetchActivity() {
        activityRepository.fetchActivity(_activityItemLiveData)
    }

    fun fetchJoinedActivities() {
        userDataRepository.fetchJoinedActivities(_joinedActivitiesLiveData)
    }

    fun fetchPredictedImages() {
//        userDataRepository.fetchPredictedImages(joinedActivity)
    }
}