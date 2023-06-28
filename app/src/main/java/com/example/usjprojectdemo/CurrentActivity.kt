package com.example.usjprojectdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usjprojectdemo.Adapter.PredictedImageExhibitAdapter
import com.example.usjprojectdemo.Data.DataViewModel
import com.example.usjprojectdemo.Data.UserData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.w3c.dom.Text

class CurrentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current)


        val viewModel = ViewModelProvider(this)[DataViewModel::class.java]
        viewModel.fetchJoinedActivities()
        val extras = intent.extras
        val position = extras?.getInt("position")
        val id = extras?.getString("id")
        val title = extras?.getString("title")

        val titleText = findViewById<TextView>(R.id.textView)
        titleText.text = title

        val recycleView = findViewById<RecyclerView>(R.id.recycleView)
        val adapter = PredictedImageExhibitAdapter()
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.adapter = adapter

        if (id != null) {
            UserData.currentActivityID = id
        }
        viewModel.joinedActivitiesLiveData.observe(this) { List ->
            adapter.setCurrentActivity(List[position!!])
        }


        findViewById<FloatingActionButton>(R.id.floatingButton).setOnClickListener {
            val intent = Intent(this, ObjectDetectionActivity::class.java)
            startActivity(intent)
        }
    }
}