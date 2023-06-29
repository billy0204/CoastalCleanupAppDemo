package com.example.usjprojectdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.example.usjprojectdemo.Data.PredictedImage
import com.example.usjprojectdemo.Data.UserData
import com.example.usjprojectdemo.ml.LiteModelObjectDetectionMobileObjectLocalizerV11Metadata2
import org.tensorflow.lite.support.image.TensorImage
import java.io.File
import java.io.FileOutputStream
import java.io.NotActiveException
import java.text.SimpleDateFormat
import java.util.*


class ObjectDetectionActivity : AppCompatActivity(), OnTouchListener, OnClickListener {
    lateinit var imageView: ImageView;
    lateinit var tempImageView: ImageView;
    private val detectedObjects = ArrayList<Rect>()
    private var startPoint: PointF? = null
    private var endPoint: PointF? = null
    private var bitmap: Bitmap? = null
    private var tempBitmap: Bitmap? = null
    private lateinit var mutableBitmap: Bitmap
    private val paint = Paint()
    private lateinit var size: Point;
    private lateinit var filePath: String;
    private var drawing = false

    init {
        paint.color = Color.RED
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_object_detection)


        imageView = findViewById(R.id.imageView)
        tempImageView = findViewById(R.id.tempImageview)

        val display = windowManager.defaultDisplay
        size = Point()
        display.getSize(size)

        findViewById<Button>(R.id.nextButton).setOnClickListener(this)
        findViewById<Button>(R.id.okButton).setOnClickListener(this)
        tempImageView.setOnTouchListener(this)


        checkPermission()


    }

    private fun checkPermission() {
        val permission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(Manifest.permission.CAMERA)
        } else {
            println("Permission isGranted")
            takePicture()
        }
    }

    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        when (it) {
            true -> {
                println("Permission has been granted by user")
                takePicture()
            }
            false -> {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                //show your custom dialog and naviage to Permission seetings
            }
        }
    }

    private fun takePicture() {
        val fileName = UserData.getRandomId()
        PredictedImage.currentImage = PredictedImage(fileName!!)
        val sdf = SimpleDateFormat("hh:mm")
        PredictedImage.currentImage!!.time = sdf.format(Date())
        var fileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)


        try {
            var imageFile = File.createTempFile(fileName, ".png", fileDir)
            filePath = imageFile.absolutePath
            var uri =
                FileProvider.getUriForFile(this, "com.example.usjprojectdemo.provider", imageFile)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(intent, 100)

        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        } catch (e: NotActiveException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            bitmap = checkPictureRotation(filePath)
//            //TODO: for interface show case
//            bitmap = BitmapFactory.decodeResource(resources, R.drawable.demopic08)

            bitmap = resizeBitmap(bitmap!!)
            bitmap = Bitmap.createScaledBitmap(bitmap!!, size.x, size.y, false)
            FileOutputStream(filePath).use { out ->
                bitmap?.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    out
                )
            }
            objectDetection(bitmap)
        }
    }

    private fun resizeBitmap(bitmap: Bitmap): Bitmap {

        val ratio = Math.max(size.x.toDouble() / bitmap.width, size.y.toDouble() / bitmap.height)
        val width = (bitmap.width * ratio).toInt()
        val height = (bitmap.height * ratio).toInt()

        var resized = Bitmap.createScaledBitmap(bitmap, width, height, false)

        val startX: Int = (width - size.x) / 2
        val startY: Int = (height - size.y) / 2


        return Bitmap.createBitmap(resized, startX, startY, size.x, size.y);


    }

    private fun checkPictureRotation(photoPath: String): Bitmap? {
        val ei = ExifInterface(photoPath)
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        var bitmap: Bitmap = BitmapFactory.decodeFile(photoPath)

        var rotatedBitmap: Bitmap? = null
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap =
                rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap =
                rotateImage(bitmap, 270)
            ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
            else -> rotatedBitmap = bitmap
        }

        return rotatedBitmap
    }

    private fun drawToTempView() {
        if (!drawing) return

        if (tempBitmap == null) {
            tempBitmap = Bitmap.createBitmap(
                size.x,
                size.y,
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
            (startPoint!!.x).toInt(),
            (startPoint!!.y).toInt(),
            (endPoint!!.x).toInt(),
            (endPoint!!.y).toInt()
        )
        val canvas = Canvas(mutableBitmap)
        canvas.drawRect(rect, paint)
        detectedObjects.add(rect)

    }


    private fun objectDetection(bitmap: Bitmap?) {
        val model = LiteModelObjectDetectionMobileObjectLocalizerV11Metadata2.newInstance(this)
        val image = TensorImage.fromBitmap(bitmap)
        mutableBitmap = bitmap!!.copy(Bitmap.Config.ARGB_8888, true)


        val canvas = Canvas(mutableBitmap)

        val outputs = model.process(image)
        for (detectionResult in outputs.detectionResultList) {
            val location = detectionResult.locationAsRectF;
            val score = detectionResult.scoreAsFloat;
            if (score > 0.5) {
                canvas.drawRect(location, paint)
                val rect = Rect()
                location.round(rect)
                detectedObjects.add(rect)
            }
        }
        model.close()
        imageView.setImageBitmap(mutableBitmap)

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
            val button = view as Button
            if (!drawing) {
                drawing = true
                button.text = "Draw to picture"
            } else {
                drawToImage()
                clearTempView()
                drawing = false
                button.text = "Add object"
            }

        } else if (view!!.id == R.id.nextButton) {

            val intent = Intent(this, ObjectClassificationActivity::class.java)
            intent.putParcelableArrayListExtra("detectedRectangles", detectedObjects)
            intent.putExtra("bitmap", filePath)

            startActivity(intent)
            finish()
        }
    }

}
