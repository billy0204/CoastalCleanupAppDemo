package com.example.usjprojectdemo.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.usjprojectdemo.ObjectDetectionActivity
import com.example.usjprojectdemo.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class CollectionFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collection, container, false)

        view.findViewById<FloatingActionButton>(R.id.floatingButton).setOnClickListener{
            val intent = Intent(getActivity(), ObjectDetectionActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CollectionFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}