package com.fintrainer.fintrainer.views.discussions

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.views.discussions.fragments.FragmentDiscussions

class DiscussionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_container)

        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,FragmentDiscussions()).commit()
        }

    }
}
