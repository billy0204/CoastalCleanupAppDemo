package com.example.usjprojectdemo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class PredictedObjectExhibitActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_predicted_object_exhibit)

        val imageID = intent.extras?.getString("image")
        val rects = intent.extras?.getParcelableArrayList<Rect>("rects")
        val labels = intent.extras?.getStringArrayList("labels")
        viewPager = findViewById<ViewPager2>(R.id.viewPage)

        val adapter = ObjectAdapter(imageID!!, rects!!, labels!!, this)
        viewPager.adapter = adapter

        viewPager.offscreenPageLimit = 3
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        setUpTransformer()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

            }
        })

        val floatingButton = findViewById<FloatingActionButton>(R.id.exitButton)
        floatingButton.setOnClickListener{
            finish()
        }

    }

    private fun setUpTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + (r * 0.15f)
        }

        viewPager.setPageTransformer(transformer)
    }

    class ObjectAdapter(
        private val imageID: String,
        private val boundingBoxes: ArrayList<Rect>,
        private val labels: ArrayList<String>,
        private val context: Context
    ) :
        RecyclerView.Adapter<ObjectAdapter.ObjectViewHolder>() {

        class ObjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView = itemView.findViewById<ImageView>(R.id.imageView)
            val labelText = itemView.findViewById<TextView>(R.id.labelText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.exhibit_card, parent, false)
            return ObjectViewHolder(view)

        }

        override fun getItemCount(): Int {
            return boundingBoxes.size
        }


        override fun onBindViewHolder(holder: ObjectViewHolder, position: Int) {
            val bitmap = getPreviewBitmap(getBitmap(imageID)!!, boundingBoxes[position])
            holder.imageView.setImageBitmap(bitmap)
            holder.labelText.text = labels[position]
        }

        private fun getBitmap(fileName: String): Bitmap? {
            Log.d("Demotest", fileName)
            val path: String = context.filesDir.toString() + "/"
            val image = File(path + fileName)
            var bitmap: Bitmap? = null
            if (image.exists()) {
                Log.d("Demotest", fileName + "exists")
                val fis = context.openFileInput(fileName)
                bitmap = BitmapFactory.decodeStream(fis)
                fis.close()
            }

            return bitmap
        }

        private fun getPreviewBitmap(bitmap: Bitmap, rect: Rect): Bitmap {
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
    }
}