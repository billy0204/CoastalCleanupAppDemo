package com.example.usjprojectdemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.usjprojectdemo.Adapter.ClassifyCardAdapter
import java.io.File


class ObjectClassificationActivity : AppCompatActivity() {

    lateinit var viewPage: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_object_classification)

        viewPage = findViewById(R.id.viewPage)


        val extras = intent.extras
        val detectedRectangles = extras?.getParcelableArrayList<Rect>("detectedRectangles")
        val fileName = intent.getStringExtra("bitmap")
        var bitmap: Bitmap
        val path: String = getFilesDir().toString() + "/"
        val image = File(path + fileName)
        if (image.exists()) {
            val fis = openFileInput(fileName)
            bitmap = BitmapFactory.decodeStream(fis)
            val adapter = detectedRectangles?.let { ClassifyCardAdapter(it, bitmap) }
            viewPage.adapter = adapter
        }

    }
}