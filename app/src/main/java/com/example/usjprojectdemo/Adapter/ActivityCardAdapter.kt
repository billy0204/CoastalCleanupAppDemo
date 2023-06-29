package com.example.usjprojectdemo.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.usjprojectdemo.CreateActivity
import com.example.usjprojectdemo.CurrentActivity
import com.example.usjprojectdemo.Data.ActivityItem
import com.example.usjprojectdemo.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class ActivityCardAdapter(private val context: Context) :
    RecyclerView.Adapter<ActivityCardAdapter.ActivityCardHolder>() {
    private val activityItems = mutableListOf<ActivityItem>()

    class ActivityCardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val previewMap: ImageView = itemView.findViewById(R.id.previewMap)
        val title: TextView = itemView.findViewById(R.id.titleText)
        val location: TextView = itemView.findViewById(R.id.locationText)
        val time: TextView = itemView.findViewById(R.id.timeText)
        val editButton:Button = itemView.findViewById(R.id.editButton)
        val deleteButton:Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityCardHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_card, parent, false)

        return ActivityCardHolder(view)
    }

    override fun getItemCount(): Int {
        return activityItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(activityItems: List<ActivityItem>) {
        Log.d("test","Set")
        this.activityItems.clear()
        this.activityItems.addAll(activityItems)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ActivityCardHolder, position: Int) {
        val activity = activityItems.get(position)
        holder.title.text = activity.activity_name
        holder.time.text = activity.date + "\n" + activity.starting_time + "~" + activity.end_time
        holder.location.text = activity.location_name

        val fileName = "${activity.latitude.toString() + "_" + activity.longitude}"

        val path: String = context.getFilesDir().toString() + "/"
        val image = File(path + fileName)
        if (image.exists()) {
            val fis = context.openFileInput(fileName)
            holder.previewMap.setImageBitmap(BitmapFactory.decodeStream(fis))
        } else {
            downloadImage(fileName, holder.previewMap)
        }


        holder.editButton.setOnClickListener{
            val intent = Intent(context, CreateActivity::class.java)
            intent.putExtra("id", activity.id)
            intent.putExtra("date",activity.date)
            intent.putExtra("title",activity.activity_name)
            intent.putExtra("start",activity.starting_time)
            intent.putExtra("end",activity.end_time)
            intent.putExtra("location",activity.location_name)
            intent.putExtra("lat",activity.latitude)
            intent.putExtra("lng",activity.longitude)
            context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("ISE")
            myRef.child(activity.id.toString())
                .removeValue()
        }

    }

    private fun downloadImage(fileName: String, previewMap: ImageView) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bytes = FirebaseStorage.getInstance().reference.child("previewMap/" + fileName)
                    .getBytes(5L * 1024 * 1024).await()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)


                val fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
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