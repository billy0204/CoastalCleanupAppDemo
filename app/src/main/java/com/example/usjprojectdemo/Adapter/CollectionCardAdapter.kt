package com.example.usjprojectdemo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.usjprojectdemo.Data.DataViewModel
import com.example.usjprojectdemo.Data.JoinedActivity
import com.example.usjprojectdemo.R

class CollectionCardAdapter(private val joinedActivities: ArrayList<JoinedActivity>):RecyclerView.Adapter<CollectionCardAdapter.CollectionCardViewHolder>() {
    class CollectionCardViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.collection_card, parent, false)
        return CollectionCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return joinedActivities.size
    }

    override fun onBindViewHolder(holder: CollectionCardViewHolder, position: Int) {

    }
}