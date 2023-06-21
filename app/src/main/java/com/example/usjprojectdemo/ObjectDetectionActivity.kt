package com.example.usjprojectdemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.Display.Mode
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.usjprojectdemo.ml.LiteModelObjectDetectionMobileObjectLocalizerV11Metadata2
import com.example.usjprojectdemo.ml.MobileFp16
import com.example.usjprojectdemo.ml.SsdMobilenetV11Metadata1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage


class ObjectDetectionActivity : AppCompatActivity(), OnTouchListener, OnClickListener {
    lateinit var imageView: ImageView;
    lateinit var tempImageView: ImageView;
    private val detectedObjects = ArrayList<Rect>()
    private var startPoint: PointF? = null
    private var endPoint: PointF? = null
    private var tempBitmap: Bitmap? = null
    private lateinit var mutableBitmap: Bitmap
    private val paint = Paint()

    init {
        paint.color = Color.RED
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_object_detection)

        imageView = findViewById(R.id.imageView)
        tempImageView = findViewById(R.id.tempImageview)
        val original = BitmapFactory.decodeResource(resources, R.drawable.bottle_02)

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val bitmap = Bitmap.createScaledBitmap(original, size.x, size.y, false)
        objectDetection(bitmap)


        val fileName = "testBitmap.png"
        CoroutineScope(Dispatchers.IO).launch {
            val fos = openFileOutput(fileName, Context.MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close();
        }

        findViewById<Button>(R.id.nextButton).setOnClickListener(this)
        findViewById<Button>(R.id.okButton).setOnClickListener(this)

        tempImageView.setOnTouchListener(this)
    }


    private fun drawToTempView() {
        if (tempBitmap == null) {
            tempBitmap = Bitmap.createBitmap(
                tempImageView.width,
                tempImageView.height,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(tempBitmap!!)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        var paint = Paint()
        paint.color = Color.GREEN
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f

        canvas.drawRect(startPoint!!.x, startPoint!!.y, endPoint!!.x, endPoint!!.y, paint)
        Log.d("draw", startPoint.toString() + "_" + endPoint.toString())
        tempImageView.setImageBitmap(tempBitmap)
    }

    private fun clearTempView() {
        val canvas = Canvas(tempBitmap!!)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        tempImageView.setImageBitmap(tempBitmap)

    }

    private fun drawToImage() {
        val rect = Rect(
            startPoint!!.x.toInt(),
            startPoint!!.y.toInt(),
            endPoint!!.x.toInt(),
            endPoint!!.y.toInt()
        )
        val canvas = Canvas(mutableBitmap)
        canvas.drawRect(rect, paint)
        detectedObjects.add(rect)

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


        val rect = rectList.get(0)

        val resizedBmp: Bitmap =
            Bitmap.createBitmap(
                mutableBitmap,
                (rect.centerX() - rect.width() / 2),
                (rect.centerY() - rect.height() / 2),
                rect.width(),
                rect.height()
            )

// Releases model resources if no longer used.
        model.close()
    }


    private fun classifyModel2(bitmap: Bitmap) {
        val model = LiteModelObjectDetectionMobileObjectLocalizerV11Metadata2.newInstance(this)

// Creates inputs for reference.
        val image = TensorImage.fromBitmap(bitmap)
        val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        var paint = Paint()
        paint.color = Color.RED
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f

        val canvas = Canvas(mutableBitmap)

// Runs model inference and gets result.
        val outputs = model.process(image)

        for (detectionResult in outputs.detectionResultList) {
            val location = detectionResult.locationAsRectF;
            val category = detectionResult.categoryAsString;
            val score = detectionResult.scoreAsFloat;

            if (score > 0.5) {
                canvas.drawRect(location, paint)
            }
        }


// Releases model resources if no longer used.
        model.close()

    }

    private fun objectDetection(bitmap: Bitmap) {
        val model = LiteModelObjectDetectionMobileObjectLocalizerV11Metadata2.newInstance(this)
        val image = TensorImage.fromBitmap(bitmap)
        mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)


        val canvas = Canvas(mutableBitmap)

        val outputs = model.process(image)
        for (detectionResult in outputs.detectionResultList) {
            val location = detectionResult.locationAsRectF;
            val score = detectionResult.scoreAsFloat;
            Log.d("model",score.toString())
            if (score > 0.4) {
                canvas.drawRect(location, paint)

                val rect = Rect()
                location.round(rect)
                detectedObjects.add(rect)
            }
        }
        model.close()
        imageView.setImageBitmap(mutableBitmap)

    }


    private fun testModel2(bitmap: Bitmap) {
        val model = MobileFp16.newInstance(this)

// Creates inputs for reference.
        val image = TensorImage.fromBitmap(bitmap)

// Runs model inference and gets result.
        val outputs = model.process(image)
        val probability = outputs.probabilityAsCategoryList
        Log.d("model", probability.toString())
// Releases model resources if no longer used.
        model.close()
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        if (view!!.id == R.id.tempImageview) {
            when (event!!.action) {

                MotionEvent.ACTION_DOWN -> {
                    startPoint = PointF(event.x, event.y)
                    endPoint = PointF()
                }
                MotionEvent.ACTION_MOVE -> {
                    endPoint!!.x = event.x
                    endPoint!!.y = event.y
                    drawToTempView()
                }
                MotionEvent.ACTION_UP -> {
                    endPoint!!.x = event.x
                    endPoint!!.y = event.y
                    drawToTempView()
                }
            }
            return true
        }

        return false
    }

    override fun onClick(view: View?) {
        if (view!!.id == R.id.okButton) {
            drawToImage()
            clearTempView()

        } else if (view!!.id == R.id.nextButton) {
            //TODO: set filename programmatically
            val fileName = "testBitmap.png"

            val intent = Intent(this, ObjectClassificationActivity::class.java)

            intent.putParcelableArrayListExtra("detectedRectangles", detectedObjects)
            intent.putExtra("bitmap", fileName)

            startActivity(intent)
        }
    }

}
