package com.example.usjprojectdemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.usjprojectdemo.Adapter.ClassifyCardAdapter
import com.example.usjprojectdemo.Data.PredictedImage
import com.example.usjprojectdemo.Data.PredictedObject
import com.example.usjprojectdemo.Data.UserData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class ObjectClassificationActivity : AppCompatActivity() {
    lateinit var viewPage: ViewPager2
    lateinit var filePath: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_object_classification)

        viewPage = findViewById(R.id.viewPage)

        val extras = intent.extras
        val detectedRectangles = extras?.getParcelableArrayList<Rect>("detectedRectangles")

        if (detectedRectangles != null) {
            for (rect in detectedRectangles) {
                val predictedObject = PredictedObject(rect, "")
                PredictedImage.currentImage!!.objects.add(predictedObject)
            }
        }

        filePath = intent.getStringExtra("bitmap").toString()
        var bitmap: Bitmap = BitmapFactory.decodeFile(filePath)
        val adapter =  ClassifyCardAdapter(bitmap, this)
        viewPage.adapter = adapter

        val button = findViewById<Button>(R.id.doneButton)
        button.setOnClickListener {
            uploadClassifyResult()
        }
    }


    private fun uploadClassifyResult() {
        Log.d("model", filePath)

        val database = FirebaseDatabase.getInstance()

        val userRef = database.getReference("users").child(UserData.user.id).child("JoinedActivity").child(UserData.currentActivityID)
        var imagesRef: StorageReference? = Firebase.storage.reference.child("Images")
            .child(UserData.randomID!!)

        userRef.child(UserData.randomID!!).setValue(PredictedImage.currentImage)


        val baos = ByteArrayOutputStream()
        BitmapFactory.decodeFile(filePath).compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()
        var uploadTask = imagesRef?.putBytes(data)
        uploadTask?.addOnFailureListener {
            // Handle unsuccessful uploads
        }?.addOnSuccessListener { taskSnapshot ->
            Log.d("demo", "success uploaded")
        }


    }
}