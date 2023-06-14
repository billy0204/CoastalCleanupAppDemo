package com.example.usjprojectdemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ScheduleFragment : Fragment() {

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

    private fun initRecycleView(view: View){
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleView)
        val layoutManager = LinearLayoutManager(context)
        val adapter = UserActivityCardAdapter(requireContext())

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        viewModel.activityItemLiveData.observe(viewLifecycleOwner){items->
            adapter.setItems(items)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScheduleFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}