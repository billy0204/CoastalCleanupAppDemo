package com.example.usjprojectdemo.Data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserDataRepository {
    private val database = FirebaseDatabase.getInstance()
    private val joinedActivityRef = database.getReference("users").child(UserData.user.id).child("JoinedActivity")

    fun fetchJoinedActivities(liveData: MutableLiveData<List<UserData>>){
        joinedActivityRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val activityDataForUser: List<ActivityDataForUser> = snapshot.children.map { dataSnapshot ->
                    dataSnapshot.getValue(ActivityDataForUser::class.java)!!
                }
                liveData.postValue(activityDataForUser)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}