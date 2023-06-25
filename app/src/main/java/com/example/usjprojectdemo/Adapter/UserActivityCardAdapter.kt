package com.example.usjprojectdemo.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.usjprojectdemo.Data.ActivityItem
import com.example.usjprojectdemo.Data.JoinedActivity
import com.example.usjprojectdemo.Data.UserData
import com.example.usjprojectdemo.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class UserActivityCardAdapter(
    private val context: Context,
    private val listener: JoinedChangeListener
) : RecyclerView.Adapter<UserActivityCardAdapter.CardHolder>() {
    private val activityItems = mutableListOf<ActivityItem>()
    private val joinedActivities = mutableListOf<JoinedActivity>()

    interface JoinedChangeListener {
        fun updateJoinedActivities(ID: String)
    }

    class CardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var joined = false
        val joinButton: Button = itemView.findViewById(R.id.joinButton)
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val topDateText: TextView = itemView.findViewById(R.id.topDateText)
        val insideDateText: TextView = itemView.findViewById(R.id.insideDateText)
        val organizerText: TextView = itemView.findViewById(R.id.organizerText)
        val locationText: TextView = itemView.findViewById(R.id.locationText)
        val previewMap: ImageView = itemView.findViewById(R.id.previewMap)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_card_user, parent, false)
        return CardHolder(itemView)
    }

    override fun getItemCount(): Int {
        return activityItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(activityItems: List<ActivityItem>) {
        this.activityItems.clear()
        this.activityItems.addAll(activityItems)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setJoinedActivity(joined: List<JoinedActivity>) {
        this.joinedActivities.clear()
        this.joinedActivities.addAll(joined)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        val currentActivity = activityItems[position]
        holder.titleText.text = currentActivity.activity_name
        holder.topDateText.text = currentActivity.date
        holder.insideDateText.text =
            currentActivity.date + ", " + currentActivity.starting_time + "~" + currentActivity.end_time
        holder.locationText.text = currentActivity.location_name

        var exist = false
        for (joined in joinedActivities) {
            if (!exist && joined.id == currentActivity.id) {
                holder.joined = true
            }
        }
        setButtonColor(holder)

        holder.joinButton.setOnClickListener {
            listener.updateJoinedActivities(currentActivity.id!!)
            holder.joined = !holder.joined
            setButtonColor(holder)
        }


        val fileName = "${currentActivity.latitude.toString() + "_" + currentActivity.longitude}"

        val path: String = context.filesDir.toString() + "/"
        val image = File(path + fileName)
        if (image.exists()) {
            val fis = context.openFileInput(fileName)
            holder.previewMap.setImageBitmap(BitmapFactory.decodeStream(fis))
        } else {
            downloadImage(fileName, holder.previewMap)
        }


    }

    private fun setButtonColor(holder: CardHolder) {
        if (!holder.joined) {
            holder.joinButton.background = context.getDrawable(R.drawable.join_button)
            holder.joinButton.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.joinButton.text = "Join"
        } else {
            holder.joinButton.background = context.getDrawable(R.drawable.joined_button)
            holder.joinButton.setTextColor(ContextCompat.getColor(context, R.color.DarkBlue))
            holder.joinButton.text = "Joined"
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