package com.example.usjprojectdemo.Data

import java.io.Serializable

data class PredictedImage(
    var fileID:String ="",
    var time:String="",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    val objects:ArrayList<PredictedObject> = ArrayList<PredictedObject>()
    ): Serializable {
    companion object{
        var currentImage: PredictedImage? = null
    }
}