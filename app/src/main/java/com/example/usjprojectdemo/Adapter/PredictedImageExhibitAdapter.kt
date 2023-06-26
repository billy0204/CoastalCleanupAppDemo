package com.example.usjprojectdemo.Adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.usjprojectdemo.Data.JoinedActivity
import com.example.usjprojectdemo.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.io.File

class PredictedImageExhibitAdapter(private val joinedActivity: JoinedActivity) :
    RecyclerView.Adapter<PredictedImageExhibitAdapter.ImageCardViewHolder>() {
    lateinit var context: Context

    class ImageCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val timeText: TextView = itemView.findViewById(R.id.timeText)
        val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageCardViewHolder {
        context = parent.context
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.collected_image_card, parent, false)
        return PredictedImageExhibitAdapter.ImageCardViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return joinedActivity.images.size
    }

    override fun onBindViewHolder(holder: ImageCardViewHolder, position: Int) {
        val currentImage = joinedActivity.images[position]
        setImage(currentImage.fileID, holder)
        holder.timeText.text = currentImage.time
        holder.descriptionText.text = currentImage.objects.size.toString() + " items\n"+"collected"
    }

    private fun setImage(fileName: String, holder: ImageCardViewHolder) {
        val path: String = context.filesDir.toString() + "/"
        val image = File(path + fileName)
        if (image.exists()) {
            val fis = context.openFileInput(fileName)
            holder.imageView.setImageBitmap(BitmapFactory.decodeStream(fis))
        } else {
            downloadImage(fileName, holder.imageView)
        }
    }


    private fun downloadImage(fileName: String, previewMap: ImageView) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bytes = FirebaseStorage.getInstance().reference.child("Images/" + fileName)
                    .getBytes(5L * 1080 * 1920).await()
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