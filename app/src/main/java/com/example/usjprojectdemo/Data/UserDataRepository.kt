package com.example.usjprojectdemo.Data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserDataRepository {
    private val database = FirebaseDatabase.getInstance()
    private val userRef = database.getReference("users").child(UserData.user.id)

    //    fun fetchPredictedImages(joinedActivity: JoinedActivity){
//        joinedActivityRef.child(joinedActivity.activityID!!).addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val activityDataForUser: List<PredictedImage> = snapshot.children.map { dataSnapshot ->
//                    dataSnapshot.getValue(PredictedImage::class.java)!!
//                }
//                liveData.postValue(activityDataForUser)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//
//        })
//    }
//
    fun fetchJoinedActivities(liveData: MutableLiveData<List<JoinedActivity>>) {
        userRef.child("JoinedActivities").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val joinedActivities: List<JoinedActivity> =
                    snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(JoinedActivity::class.java)!!
                    }
                liveData.postValue(joinedActivities)
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


}