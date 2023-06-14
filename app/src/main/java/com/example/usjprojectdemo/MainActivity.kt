package com.example.usjprojectdemo

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        val viewModel = ViewModelProvider(this)[DataViewModel::class.java]
        viewModel.fetchActivity()

        val organizer = OrganizerFragment()
        val scheduleFragment = ScheduleFragment()

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.organizerNav -> makeCurrentFragment(organizer)
                R.id.ScheduleNav -> makeCurrentFragment(scheduleFragment)

                else -> {}
            }
            true
        }



        makeCurrentFragment(organizer)
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


