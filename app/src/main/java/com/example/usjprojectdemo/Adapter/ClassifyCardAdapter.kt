package com.example.usjprojectdemo.Adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.Resource
import com.example.usjprojectdemo.R
import java.security.AccessController.getContext


class ClassifyCardAdapter(private val objects: ArrayList<Rect>, private val bitmap: Bitmap) :
    RecyclerView.Adapter<ClassifyCardAdapter.ClassifyCardHolder>() {


    class ClassifyCardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.classifyObjectImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassifyCardHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.classification_card, parent, false)

        return ClassifyCardHolder(view)
    }

    override fun getItemCount(): Int {
        return objects.size
    }

    override fun onBindViewHolder(holder: ClassifyCardHolder, position: Int) {
        val rect = objects[position]
        var topX = rect.centerX() - rect.width() / 2
        var topY = rect.centerY() - rect.height() / 2
        if (topX < 0) topX = 0
        if (topY < 0) topY = 0
        val objectBitmap: Bitmap =
            Bitmap.createBitmap(
                bitmap,
                topX,
                topY,
                rect.width(),
                rect.height()
            )

        val size = Math.max(rect.width(), rect.height())
        topX = (rect.centerX() - size / 2)
        topY = (rect.centerY() - size / 2)
        if (topX < 0) topX = 0
        if (topY < 0) topY = 0
        if ((topY + size) > bitmap.height) topY = bitmap.height - size
        if ((topX + size) > bitmap.width) topX = bitmap.width - size

        Log.d("model",topX.toString() +"_"+topY.toString())
        val objectPreview: Bitmap =
            Bitmap.createBitmap(
                bitmap,
                topX,
                topY,
                size,
                size
            )

        holder.imageView.setImageBitmap(objectPreview)
    }


}