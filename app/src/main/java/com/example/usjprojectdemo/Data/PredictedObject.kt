package com.example.usjprojectdemo.Data

import android.graphics.Rect

data class PredictedObject(
    val boundingBox:Rect = Rect(),
    var label:String ="",

    )