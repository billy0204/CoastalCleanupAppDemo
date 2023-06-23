package com.example.usjprojectdemo.Data

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

data class UserData(
    var id: String = "",
    var userName: String? =null,
    var workingHour: Int? =null,
    var pickedNumber: Int? =null,
    var collectedPoint: Int? =null,
    var joinedActivities:ArrayList<ActivityDataForUser> = ArrayList<ActivityDataForUser>()
){
    companion object{
        val user:UserData = UserData()
        val currentActivityID:String = "1686618882136";
        var randomID = getRandomId()
        fun getRandomId(): String? {
            val userRef = Firebase.database.getReference("users").child(user.id).child("JoinedActivity").child(UserData.currentActivityID)
            val id: String? = userRef.push().getKey()
            return id
        }
    }
}
