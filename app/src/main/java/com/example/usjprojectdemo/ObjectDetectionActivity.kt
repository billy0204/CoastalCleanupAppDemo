package com.example.usjprojectdemo

import android.graphics.*
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.usjprojectdemo.ml.SsdMobilenetV11Metadata1
import org.tensorflow.lite.support.image.TensorImage


class ObjectDetectionActivity : AppCompatActivity() {

    lateinit var textView: TextView
    lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_object_detection)

        textView = findViewById(R.id.tvPlaceholder)
        imageView = findViewById(R.id.imageView)

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.dog_cat)

        classifyImage(bitmap)
    }

    private fun classifyImage(bitmap: Bitmap) {
        val model = SsdMobilenetV11Metadata1.newInstance(this)

// Creates inputs for reference.
        val image = TensorImage.fromBitmap(bitmap)

// Runs model inference and gets result.
        val outputs = model.process(image)

        val locations = outputs.locationsAsTensorBuffer
        val classes = outputs.classesAsTensorBuffer
        val scores = outputs.scoresAsTensorBuffer
        val numberOfDetections = outputs.numberOfDetectionsAsTensorBuffer

        val labelArray = scores.floatArray
        val locationArray = locations.floatArray

        val h = bitmap.height
        val w = bitmap.width
        var index = 0;

        val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)


        var paint = Paint()
        paint.color = Color.RED
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f

        val canvas = Canvas(mutableBitmap)

        var rectList = mutableListOf<Rect>()

        for (label in labelArray) {

            val top = locationArray.get(index++)
            val left = locationArray.get(index++)
            val bottom = locationArray.get(index++)
            val right = locationArray.get(index++)

            val rect = Rect(
                (left * w).toInt(), (top * h).toInt(), (right * w).toInt(),
                (bottom * h).toInt()
            )
            if (label >= 0.5f) {
                rectList.add(rect)
                canvas.drawRect(left * w, top * h, right * w, bottom * h, paint)
            }
        }


//        textView.text =
//            "Number of object detected: " + numberOfDetections.floatArray.get(0).toString()


        val rect = rectList.get(0)

        val resizedBmp: Bitmap =
            Bitmap.createBitmap(
                mutableBitmap,
                (rect.centerX() - rect.width() / 2),
                (rect.centerY() - rect.height() / 2),
                rect.width(),
                rect.height()
            )
        imageView.setImageBitmap(mutableBitmap)


// Releases model resources if no longer used.
        model.close()
    }


}
