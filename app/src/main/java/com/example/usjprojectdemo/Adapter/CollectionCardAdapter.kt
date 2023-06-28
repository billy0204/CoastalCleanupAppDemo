package com.example.usjprojectdemo.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.usjprojectdemo.CurrentActivity
import com.example.usjprojectdemo.Data.ActivityItem
import com.example.usjprojectdemo.Data.JoinedActivity
import com.example.usjprojectdemo.ObjectClassificationActivity
import com.example.usjprojectdemo.R

class CollectionCardAdapter(private val context: Context) :
    RecyclerView.Adapter<CollectionCardAdapter.CollectionCardViewHolder>() {
    private val joinedActivities = mutableListOf<JoinedActivity>()
    private val activityList = mutableListOf<ActivityItem>()

    class CollectionCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleText)
        val location: TextView = itemView.findViewById(R.id.locationText)
        val time: TextView = itemView.findViewById(R.id.timeText)
        val date: TextView = itemView.findViewById(R.id.dateText)
        val cardView: View = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionCardViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.collection_card, parent, false)
        return CollectionCardViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setJoinedActivities(joined: List<JoinedActivity>) {
        this.joinedActivities.clear()
        this.joinedActivities.addAll(joined)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setActivityList(list: List<ActivityItem>) {
        this.activityList.clear()
        this.activityList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return joinedActivities.size
    }

    override fun onBindViewHolder(holder: CollectionCardViewHolder, position: Int) {
        val currentActivity = joinedActivities[position]
        val currentID = currentActivity.id
        for (activity in activityList) {
            if (activity.id == currentID) {
                holder.title.text = activity.activity_name
                holder.location.text = activity.location_name
                holder.time.text = activity.starting_time + "~" + activity.end_time
                holder.date.text = activity.date


                holder.cardView.setOnClickListener {
                    val intent = Intent(context, CurrentActivity::class.java)
                    intent.putExtra("position", position)
                    intent.putExtra("id", activity.id)
                    intent.putExtra("title",activity.activity_name)

                    context.startActivity(intent)
                }
            }
        }
    }
}