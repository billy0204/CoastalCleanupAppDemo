package com.example.usjprojectdemo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ActivityRepository {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("ISE")


    fun fetchActivity(liveData: MutableLiveData<List<ActivityItem>>){
        Log.d("test","fetchActivity")

        myRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val activityItem: List<ActivityItem> = snapshot.children.map { dataSnapshot ->
                    dataSnapshot.getValue(ActivityItem::class.java)!!
                }
                Log.d("test",snapshot.toString())
                liveData.postValue(activityItem)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
