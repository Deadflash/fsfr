package com.fintrainer.fintrainer.views.testing.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.adapters.AnswersAdapter
import com.fintrainer.fintrainer.utils.Constants.TESTING_FRAGMENT_TAG
import com.fintrainer.fintrainer.views.BaseFragment
import com.fintrainer.fintrainer.views.result.ResultActivity
import kotlinx.android.synthetic.main.fragment_testing.*
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.support.v4.toast

/**
 * Created by krotk on 22.10.2017.
 */
class TestingFragment : BaseFragment(),AnswersAdapter.IAnswers {

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean = false
        }
        recycler.adapter = AnswersAdapter(this)
        recycler.clearFocus()
        recycler.isNestedScrollingEnabled = false

    }

    override fun onAnswerClicked(position: Int, isRight: Boolean) {
        startActivityForResult<ResultActivity>(2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        toast("Result Code $resultCode Request Code $requestCode")
    }

    override fun getFragmentLayout(): Int = R.layout.fragment_testing

    override fun getFragmentTag(): String = TESTING_FRAGMENT_TAG
}