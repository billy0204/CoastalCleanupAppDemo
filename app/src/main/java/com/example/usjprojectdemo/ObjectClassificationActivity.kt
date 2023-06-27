package com.example.usjprojectdemo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.usjprojectdemo.Adapter.ClassifyCardAdapter
import com.example.usjprojectdemo.Data.JoinedActivity
import com.example.usjprojectdemo.Data.PredictedImage
import com.example.usjprojectdemo.Data.PredictedObject
import com.example.usjprojectdemo.Data.UserData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class ObjectClassificationActivity : AppCompatActivity() {
    lateinit var viewPager: ViewPager2
    lateinit var filePath: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_object_classification)

        viewPager = findViewById(R.id.viewPage)

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
        val adapter = ClassifyCardAdapter(bitmap, this)
        viewPager.adapter = adapter


        viewPager.offscreenPageLimit = 3
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        setUpTransformer()

        val button = findViewById<Button>(R.id.doneButton)
        button.setOnClickListener {
            button.isClickable = false
            uploadClassifyResult()
            finish()
        }
    }
    private fun setUpTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(20))
        transformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + (r * 0.15f)
        }

        viewPager.setPageTransformer(transformer)
    }


    private fun uploadClassifyResult() {

        val database = FirebaseDatabase.getInstance()

        val joinedRef =
            database.getReference("users").child(UserData.user.id).child("JoinedActivities")


        var exist = false
        joinedRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    val joined = snap.getValue(JoinedActivity::class.java)
                    if (!exist && joined!!.id == UserData.currentActivityID) {
                        joined.images.add(PredictedImage.currentImage!!)
                        joinedRef.child(snap.key.toString()).setValue(joined)
                        exist = true
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


        val baos = ByteArrayOutputStream()
        val bitmap = BitmapFactory.decodeFile(filePath)

        val fos = this.openFileOutput(PredictedImage.currentImage!!.fileID, Context.MODE_PRIVATE)
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close();


        var imagesRef: StorageReference? = Firebase.storage.reference.child("Images")
            .child(PredictedImage.currentImage!!.fileID)
        bitmap.compress(Bitmap.CompressFormat.PNG, 75, baos)
        val data = baos.toByteArray()
        var uploadTask = imagesRef?.putBytes(data)
        uploadTask?.addOnFailureListener {
            // Handle unsuccessful uploads
        }?.addOnSuccessListener { taskSnapshot ->
            baos.close()
        }


    }
}