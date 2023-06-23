package com.example.usjprojectdemo.Adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.usjprojectdemo.Data.DataViewModel
import com.example.usjprojectdemo.Data.PredictedImage
import com.example.usjprojectdemo.Data.PredictedObject
import com.example.usjprojectdemo.MainActivity
import com.example.usjprojectdemo.R
import com.example.usjprojectdemo.ml.MobileFp16
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category


class ClassifyCardAdapter(
    private val bitmap: Bitmap,
    private val context: Context
) :
    RecyclerView.Adapter<ClassifyCardAdapter.ClassifyCardHolder>() {


    class ClassifyCardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.classifyObjectImage)
        val radioGroup = itemView.findViewById<RadioGroup>(R.id.radioGroup)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassifyCardHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.classification_card, parent, false)

        return ClassifyCardHolder(view)
    }

    override fun getItemCount(): Int {
        return PredictedImage.currentImage!!.objects.size
    }

    override fun onBindViewHolder(holder: ClassifyCardHolder, position: Int) {
        val rect = PredictedImage.currentImage!!.objects[position].boundingBox
        val labels = getLabelsFromObject(getClassifyBitmap(rect, 224, 224))

        for (i in 0 until holder.radioGroup.getChildCount()) {
            val text = labels[i].label + "(" + (labels[i].score * 100).toInt() + "%)"
            (holder.radioGroup.getChildAt(i) as RadioButton).text = text
        }
        holder.radioGroup.setOnCheckedChangeListener{group,checkId->
            val button = group.findViewById<RadioButton>(checkId)
            PredictedImage.currentImage!!.objects[position].label = button.text.toString().substring(0,button.text.indexOf('('))

        }
        holder.imageView.setImageBitmap(getPreviewBitmap(rect))
    }

    private fun getClassifyBitmap(rect: Rect, height: Int, width: Int): Bitmap {
        var topX = rect.centerX() - rect.width() / 2
        var topY = rect.centerY() - rect.height() / 2
        if (topX < 0) topX = 0
        if (topY < 0) topY = 0
        var objectBitmap: Bitmap =
            Bitmap.createBitmap(
                bitmap,
                topX,
                topY,
                rect.width(),
                rect.height()
            )

        objectBitmap = Bitmap.createScaledBitmap(objectBitmap, height, width, false)
        return objectBitmap
    }

    private fun getPreviewBitmap(rect: Rect): Bitmap {
        var topX = rect.centerX() - rect.width() / 2
        var topY = rect.centerY() - rect.height() / 2
        val size = Math.max(rect.width(), rect.height())
        if (topX < 0) topX = 0
        if (topY < 0) topY = 0
        if ((topY + size) > bitmap.height) topY = bitmap.height - size
        if ((topX + size) > bitmap.width) topX = bitmap.width - size

        Log.d("model", topX.toString() + "_" + topY.toString())
        val objectPreview: Bitmap =
            Bitmap.createBitmap(
                bitmap,
                topX,
                topY,
                size,
                size
            )

        return objectPreview
    }



    private fun getLabelsFromObject(objectBitmap: Bitmap): List<Category> {
        val model = MobileFp16.newInstance(context)

        val image = TensorImage.fromBitmap(objectBitmap)

        val outputs = model.process(image)
        val probability = outputs.probabilityAsCategoryList

        probability.sortByDescending { it.score }
        Log.d("model", probability.toString())
        var labelList = probability.take(5)

        model.close()
        return labelList
    }

}