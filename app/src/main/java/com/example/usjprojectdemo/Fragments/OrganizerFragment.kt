package com.example.usjprojectdemo.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.usjprojectdemo.Adapter.ActivityCardAdapter
import com.example.usjprojectdemo.CreateActivity
import com.example.usjprojectdemo.Data.DataViewModel
import com.example.usjprojectdemo.R


class OrganizerFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    lateinit var viewModel: DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[DataViewModel::class.java]
        viewModel.fetchActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_organizer, container, false)
        initRecycleCardView(view)

        view.findViewById<View>(R.id.createActivityButton).setOnClickListener {
            createActivity()
        }

        return view
    }



    private fun createActivity() {
        val intent = Intent(activity, CreateActivity::class.java)
        startActivity(intent)
    }


    private fun initRecycleCardView(view: View) {

        viewPager = view.findViewById(R.id.pageForActivityCard)
        val adapter = ActivityCardAdapter(requireContext())

        viewPager.adapter = adapter
        viewModel.activityItemLiveData.observe(viewLifecycleOwner) { items ->
            adapter.setItems(items)
        }


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
    }


    private fun setUpTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(30))
        transformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + (r * 0.15f)
        }

        viewPager.setPageTransformer(transformer)
    }

}