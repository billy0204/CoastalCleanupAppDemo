package com.example.usjprojectdemo

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.usjprojectdemo.Data.DataViewModel
import com.example.usjprojectdemo.Data.JoinedActivity
import com.example.usjprojectdemo.Data.UserData
import com.example.usjprojectdemo.Fragments.CollectionFragment
import com.example.usjprojectdemo.Fragments.OrganizerFragment
import com.example.usjprojectdemo.Fragments.ScheduleFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)


        val viewModel =ViewModelProvider(this)[DataViewModel::class.java]


        UserData.user.id ="12121212"
        viewModel.fetchActivity()

        val organizerFragment = OrganizerFragment()
        val scheduleFragment = ScheduleFragment()
        val collectionFragment = CollectionFragment()


        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.organizerNav -> makeCurrentFragment(organizerFragment)
                R.id.ScheduleNav -> makeCurrentFragment(scheduleFragment)
                R.id.collectionNav -> makeCurrentFragment(collectionFragment)

                else -> {}
            }
            true
        }

        makeCurrentFragment(organizerFragment)
    }

    private fun makeCurrentFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction().setCustomAnimations(
            androidx.appcompat.R.anim.abc_slide_in_top,
            androidx.appcompat.R.anim.abc_fade_out,
            androidx.appcompat.R.anim.abc_fade_in,
            androidx.appcompat.R.anim.abc_slide_out_top
        ).apply {

            replace(R.id.mainFragment, fragment)
            commit()
        }
    }


}


