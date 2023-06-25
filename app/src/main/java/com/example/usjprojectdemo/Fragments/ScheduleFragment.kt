package com.example.usjprojectdemo.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usjprojectdemo.Data.DataViewModel
import com.example.usjprojectdemo.R
import com.example.usjprojectdemo.Adapter.UserActivityCardAdapter
import com.example.usjprojectdemo.Data.ActivityItem
import com.example.usjprojectdemo.Data.JoinedActivity
import com.example.usjprojectdemo.Data.UserData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ScheduleFragment : Fragment(), UserActivityCardAdapter.JoinedChangeListener {

    lateinit var viewModel: DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        viewModel = ViewModelProvider(requireActivity())[DataViewModel::class.java]
        viewModel.fetchActivity()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycleView(view)
    }

    private fun initRecycleView(view: View) {

        val viewModel = ViewModelProvider(this)[DataViewModel::class.java]
        viewModel.fetchActivity()
        viewModel.fetchJoinedActivities()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleView)
        val layoutManager = LinearLayoutManager(context)
        val adapter = UserActivityCardAdapter(requireContext(), this)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        viewModel.activityItemLiveData.observe(viewLifecycleOwner) { items ->
            adapter.setItems(items)
        }
        viewModel.joinedActivitiesLiveData.observe(viewLifecycleOwner) { joined ->
            adapter.setJoinedActivity(joined)
        }
    }

    override fun updateJoinedActivities(ID: String) {
        var exist = false
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("users").child(UserData.user.id)
            .child("JoinedActivities")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val joinedActivities = snapshot.children.map { dataSnapshot ->
                    dataSnapshot.getValue(JoinedActivity::class.java)
                }
                if (joinedActivities.size > 0) {
                    for (i in 0..joinedActivities.size - 1) {
                        if (joinedActivities[i]!!.id == ID) {
                            ref.child(joinedActivities[i]!!.id!!).removeValue()
                            exist = true
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        if (!exist) {
            val joined = JoinedActivity()
            joined.id = ID
            ref.push().setValue(joined)
        }
    }

}