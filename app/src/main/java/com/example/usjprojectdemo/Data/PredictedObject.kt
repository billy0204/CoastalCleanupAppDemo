package com.example.usjprojectdemo.Data

import android.graphics.Rect

data class PredictedObject(
    val fileName:String,
    val boundingBox:Rect = Rect(),
    val label:String,

)