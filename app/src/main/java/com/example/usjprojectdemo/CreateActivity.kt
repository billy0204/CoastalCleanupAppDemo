package com.example.usjprojectdemo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.usjprojectdemo.Data.ActivityItem
import com.example.usjprojectdemo.Data.UserData
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CreateActivity : AppCompatActivity() {

    private var activity: ActivityItem? = ActivityItem()
    private var macau = LatLng(22.122702855620894, 113.57135407626629)
    private var bitmap: Bitmap? = null

    private val intent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val lat = result.data!!.getDoubleExtra("lat", macau.latitude)
                val lng = result.data!!.getDoubleExtra("lng", macau.longitude)
                val latlng = LatLng(lat, lng)

                activity!!.latitude = latlng.latitude
                activity!!.longitude = latlng.longitude

                val urlString = buildStaticApiUrl(latlng, 15, 400, 300)


                val previewMap = findViewById<ImageView>(R.id.previewMap)
                val markerIcon = findViewById<View>(R.id.locationMarker)
                var runnable = Runnable {
                    try {
                        bitmap = Glide.with(this).asBitmap().load(urlString).submit().get()
                        runOnUiThread(Runnable {
                            Glide.with(this).load(bitmap).into(previewMap)
                            markerIcon.visibility = View.GONE
                        })
                    } catch (e: IOException) {
                        println(e)
                    }

                }

                val networkThread = Thread(runnable);
                networkThread.start()

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        val activityEditText = findViewById<EditText>(R.id.activityNameEditText)
        val locationEditText = findViewById<EditText>(R.id.locationNameEditText)
        val startingTimeText = findViewById<TextView>(R.id.startText)
        val endTimeText = findViewById<TextView>(R.id.endText)
        val negativeButton = findViewById<View>(R.id.negativeButton)
        val previewMap = findViewById<ImageView>(R.id.previewMap)
        val dateText = findViewById<TextView>(R.id.dateTextView)
        val markerIcon = findViewById<View>(R.id.locationMarker)


        val extras = getIntent().extras
        val id = extras?.getString("id")

        if (id != null) {
            activity?.id = id

            findViewById<Button>(R.id.positiveButton).text = "Save"

            activityEditText.setText(extras?.getString("title"))
            locationEditText.setText(extras?.getString("location"))
            startingTimeText.text = extras?.getString("start")
            endTimeText.text = extras?.getString("end")
            dateText.text = extras?.getString("date")
            markerIcon.visibility = View.GONE

            activity!!.latitude = extras?.getDouble("lat")!!
            activity!!.longitude = extras?.getDouble("lng")!!


            val fileName = "${activity!!.latitude.toString() + "_" + activity!!.longitude}"

            val path: String =getFilesDir().toString() + "/"
            val image = File(path + fileName)
            if (image.exists()) {
                val fis = openFileInput(fileName)
                previewMap.setImageBitmap(BitmapFactory.decodeStream(fis))
            } else {
                downloadImage(fileName, previewMap)
            }

        }else{
            activity?.id = UserData.getRandomId()
        }




        activity = ActivityItem(null);
        findViewById<View>(R.id.datePickerButton).setOnClickListener {
            showDatePicker()
        }
        findViewById<View>(R.id.startTimeButton).setOnClickListener {
            showTimePicker(startingTimeText)
        }
        findViewById<View>(R.id.endTimeButton).setOnClickListener {
            showTimePicker(endTimeText)
        }

        findViewById<View>(R.id.locationPickerButton).setOnClickListener {
            intent.launch(
                Intent(
                    this, MapPicker::class.java
                )
            )
        }

        negativeButton.setOnClickListener {
            finish()
        }


        findViewById<View>(R.id.positiveButton).setOnClickListener() {
            if (nullChecker()) {
                activity!!.activity_name = activityEditText.text.toString()
                activity!!.location_name = locationEditText.text.toString()
                activity!!.starting_time = startingTimeText.text.toString()
                activity!!.end_time = endTimeText.text.toString()
                saveActivity()
            }

        }
    }


    private fun saveActivity() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("ISE")


        myRef.child(activity?.id.toString())
            .setValue(activity)

        uploadImage()
        finish()
    }


    private fun uploadImage() {

        val fileName = activity?.latitude.toString() + "_" + activity?.longitude
        val storage = Firebase.storage
        var storageRef = storage.reference
        var imagesRef: StorageReference? = storageRef.child("previewMap")
            .child(fileName)

        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val fos = this.openFileOutput(fileName, Context.MODE_PRIVATE)
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close();


        val data = baos.toByteArray()

        var uploadTask = imagesRef?.putBytes(data)
        uploadTask?.addOnFailureListener {
            // Handle unsuccessful uploads
        }?.addOnSuccessListener { taskSnapshot ->
            Log.d("demo", "success uploaded")
        }

    }


    private fun buildStaticApiUrl(center: LatLng, zoom: Int, width: Int, height: Int): String {

        val url =
            "https://maps.googleapis.com/maps/api/staticmap?center=${center.latitude},${center.longitude}" +
                    "&zoom=${zoom}&size=${width}x${height}&key=${BuildConfig.MAPS_API_KEY}"

        return url
    }


    private fun showDatePicker() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            { view, year, monthOfYear, dayOfMonth ->


                val calendar = Calendar.getInstance()
                calendar[year, monthOfYear] = dayOfMonth
                val format = SimpleDateFormat("MMM dd, yyyy")

                val date = format.parse(format.format(calendar.time))

                val stringDate: String = format.format(date.time)
                findViewById<TextView>(R.id.dateTextView).text = stringDate
                activity?.date = stringDate

            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun showTimePicker(timeText: TextView) {
        val mTimePicker: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        mTimePicker = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                val time: String = String.format("%02d:%02d", hourOfDay, minute)
                timeText.text = time
            }
        }, hour, minute, false)

        mTimePicker.show()
    }

    private fun nullChecker(): Boolean {


        val activityEditText = findViewById<EditText>(R.id.activityNameEditText)
        val locationEditText = findViewById<EditText>(R.id.locationNameEditText)
        val previewMap = findViewById<ImageView>(R.id.previewMap)
        val dateText = findViewById<TextView>(R.id.dateTextView)


        if (TextUtils.isEmpty(activityEditText.text)) {
            activityEditText.error = "Activity name can not be empty";
            activityEditText.requestFocus()
            return false
        } else {
            activityEditText.error = null;
        }

        if (TextUtils.isEmpty(locationEditText.text)) {
            locationEditText.error = "Location can not be empty";
            locationEditText.requestFocus()
            return false
        } else {
            locationEditText.error = null;
        }


        if (TextUtils.isEmpty(locationEditText.text)) {
            locationEditText.error = "Location can not be empty";
            locationEditText.requestFocus()
            return false
        } else {
            locationEditText.error = null;
        }

        if (previewMap.drawable == null) {
            Toast.makeText(this, "Please pick the location on map", Toast.LENGTH_LONG).show()
            previewMap.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(dateText.text)) {
            Toast.makeText(this, "Please pick date of activity", Toast.LENGTH_LONG).show()
            showDatePicker()
            return false
        }

        return true
    }

    private fun downloadImage(fileName: String, previewMap: ImageView) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bytes = FirebaseStorage.getInstance().reference.child("previewMap/" + fileName)
                    .getBytes(5L * 1024 * 1024).await()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)


                val fos = openFileOutput(fileName, Context.MODE_PRIVATE)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.close();

                withContext(Dispatchers.Main) {
                    previewMap.setImageBitmap(bitmap)
                }

            } catch (e: java.lang.Exception) {
                Log.d("demo", e.toString())
            }
        }
}