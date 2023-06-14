package com.example.usjprojectdemo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.io.File

class UserActivityCardAdapter(private val context: Context):RecyclerView.Adapter<UserActivityCardAdapter.CardHolder>() {
    private val activityItems = mutableListOf<ActivityItem>()


    class CardHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val topDateText: TextView = itemView.findViewById(R.id.topDateText)
        val insideDateText: TextView = itemView.findViewById(R.id.insideDateText)
        val organizerText: TextView = itemView.findViewById(R.id.organizerText)
        val locationText: TextView = itemView.findViewById(R.id.locationText)
        val previewMap: ImageView = itemView.findViewById(R.id.previewMap)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_user,parent,false)
        return CardHolder(itemView)
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

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        val currentActivity = activityItems[position]
        holder.titleText.text = currentActivity.activity_name
        holder.topDateText.text = currentActivity.date
        holder.insideDateText.text = currentActivity.date+", "+currentActivity.starting_time+"~"+currentActivity.end_time
        holder.locationText.text = currentActivity.location_name

        val fileName = "${currentActivity.latitude.toString() + "_" + currentActivity.longitude}"

        val path: String = context.filesDir.toString() + "/"
        val image = File(path + fileName)
        if (image.exists()) {
            val fis = context.openFileInput(fileName)
            holder.previewMap.setImageBitmap(BitmapFactory.decodeStream(fis))
            Log.d("demo", "exits")
        } else {
            downloadImage(fileName, holder.previewMap)
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