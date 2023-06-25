package com.example.usjprojectdemo.Data

data class PredictedImage(
    val fileID:String,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    val objects:ArrayList<PredictedObject> = ArrayList<PredictedObject>()
    ){
    companion object{
        var currentImage: PredictedImage? = null
    }
}