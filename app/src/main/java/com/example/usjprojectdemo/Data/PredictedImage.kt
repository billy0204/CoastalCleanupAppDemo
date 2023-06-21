package com.example.usjprojectdemo.Data

data class PredictedImage(
    val fileName:String,
    val url:String,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    val objects:List<PredictedObject>
    )