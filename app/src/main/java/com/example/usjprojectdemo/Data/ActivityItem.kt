package com.example.usjprojectdemo.Data

data class ActivityItem(
    var id: String? = null,
    var activity_name: String? = null,
    var location_name: String? = null,
    var date: String? = null,
    var starting_time: String? = null,
    var end_time: String? = null,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var collaborate: List<String?>? = null,
)
