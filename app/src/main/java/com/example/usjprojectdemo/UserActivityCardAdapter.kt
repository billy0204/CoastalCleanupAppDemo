package com.example.usjprojectdemo

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class UserActivityCardAdapter:RecyclerView.Adapter<UserActivityCardAdapter.CardHolder>() {
    private val activityItems = mutableListOf<ActivityItem>()


    class CardHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val topDateText: TextView = itemView.findViewById(R.id.topDateText)
        val insideDateText: TextView = itemView.findViewById(R.id.insideDateText)
        val organizerText: TextView = itemView.findViewById(R.id.organizerText)
        val locationText: TextView = itemView.findViewById(R.id.locationText)

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
    }
}