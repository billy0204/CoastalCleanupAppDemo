package com.example.usjprojectdemo.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.usjprojectdemo.Data.JoinedActivity
import com.example.usjprojectdemo.Data.PredictedImage
import com.example.usjprojectdemo.ObjectClassificationActivity
import com.example.usjprojectdemo.PredictedObjectExhibitActivity
import com.example.usjprojectdemo.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.Serializable

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
        return ImageCardViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return joinedActivity.images.size
    }

    override fun onBindViewHolder(holder: ImageCardViewHolder, position: Int) {
        val currentImage = joinedActivity.images[position]
        setImage(currentImage.fileID, holder)
        holder.timeText.text = currentImage.time
        holder.descriptionText.text =
            currentImage.objects.size.toString() + " items\n" + "collected"

        holder.itemView.setOnClickListener {
            exhibitObjects(currentImage)
        }
    }

    private fun setImage(fileName: String, holder: ImageCardViewHolder) {
        val path: String = context.filesDir.toString() + "/"
        val image = File(path + fileName)
        if (image.exists()) {
            val fis = context.openFileInput(fileName)
            holder.imageView.setImageBitmap(BitmapFactory.decodeStream(fis))
            fis.close()
        } else {
            Log.d("downloadTest",fileName)
            downloadImage(fileName, holder.imageView)
        }
    }


    private fun downloadImage(fileName: String, previewMap: ImageView) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bytes = FirebaseStorage.getInstance().reference.child("Images/" + fileName)
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

    private fun exhibitObjects(image: PredictedImage) {
        if (image.objects.size <= 0) return
        val intent = Intent(context, PredictedObjectExhibitActivity::class.java)
        val rectList = ArrayList<Rect>()
        val labelList = ArrayList<String>()
        for (_object in image.objects) {
            rectList.add(_object.boundingBox)
            labelList.add(_object.label)
        }
        intent.putExtra("image", image.fileID)
        intent.putParcelableArrayListExtra("rects", rectList)
        intent.putExtra("labels", labelList)
        context.startActivity(intent)
    }

}