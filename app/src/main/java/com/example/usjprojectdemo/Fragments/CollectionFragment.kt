package com.example.usjprojectdemo.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usjprojectdemo.Adapter.CollectionCardAdapter
import com.example.usjprojectdemo.Data.DataViewModel
import com.example.usjprojectdemo.ObjectDetectionActivity
import com.example.usjprojectdemo.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class CollectionFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collection, container, false)


        initRecycleView(view)

        return view
    }

    private fun initRecycleView(view: View) {
        val viewModel = ViewModelProvider(this)[DataViewModel::class.java]
        viewModel.fetchActivity()
        viewModel.fetchJoinedActivities()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleView)
        val adapter = CollectionCardAdapter(requireActivity())
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        viewModel.joinedActivitiesLiveData.observe(viewLifecycleOwner){item->
            adapter.setJoinedActivities(item)
        }
        viewModel.activityItemLiveData.observe(viewLifecycleOwner){item->
            adapter.setActivityList(item)
        }
    }


}